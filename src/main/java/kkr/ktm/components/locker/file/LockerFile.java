package kkr.ktm.components.locker.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import kkr.ktm.components.locker.Locker;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsFile;
import kkr.ktm.utils.errors.StopException;

public class LockerFile extends LockerFileFwk implements Locker {
	private static final Logger LOG = Logger.getLogger(LockerFile.class);

	private static class Entity {
		private static final DateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss.SSS");
		private File file;
		private String name;
		private long time;

		public Entity(File file, String name, long time) {
			super();
			try {
				this.file = file.getCanonicalFile();
			} catch (IOException ex) {
				this.file = file;
			}
			this.name = name;
			this.time = time;
		}

		public File getFile() {
			return file;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return "[" + name + " " + dateFormat.format(new Date(time))
					+ "] ... " + file.getAbsolutePath();
		}
	}

	private static final Comparator<File> comparatorFiles = new Comparator<File>() {
		public int compare(File file1, File file2) {
			int result = new Long(file1.lastModified()).compareTo(file2
					.lastModified());
			if (result != 0) {
				return result;
			}
			return file1.getName().compareTo(file2.getName());
		}
	};

	private final FileFilter fileFilterLock = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			Matcher matcher = whoamiFilterPattern.matcher(file.getName());
			if (!matcher.matches()) {
				return false;
			}
			return true;
		}
	};

	public void lock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			checkDirectories();

			createLockFile();

			List<Entity> waitings = null;

			long waited = 0;
			int lastCount = 0;
			boolean locked = false;
			do {
				waitings = findEntities();

				if (waitings.get(0).getName().equals(file.getName())) {
					locked = true;
					break;
				}

				if (lastCount != waitings.size()) {
					printWaitings(waitings);
				}

				try {
					Thread.sleep(waitInterval);
				} catch (InterruptedException ex) {
					throw new TechnicalException(
							"Probleme to sleep the process", ex);
				}

				lastCount = waitings.size();

				waited += waitInterval;

				if (stopFile != null && stopFile.isFile()) {
					stopFile.delete();
					break;
				}

			} while (waitMax == null || waited <= waitMax);

			if (!locked) {
				LOG.warn("STOPPING the proces - I cannot wait more");
				printWaitings(waitings);
				throw new StopException("No more waiting");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void unlock() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			checkDirectories();

			if (deleteLockFile()) {
				LOG.debug("UNLOCK");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void checkDirectories() throws BaseException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new TechnicalException("Cannot create the lock directory");
			}
		}
	}

	private void createLockFile() throws BaseException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file, true);
			fileWriter.close();
			fileWriter = null;
			LOG.debug("LOCK");
		} catch (IOException ex) {
			throw new TechnicalException("Cannot create the lock file: "
					+ file.getAbsolutePath(), ex);
		} finally {
			UtilsFile.getInstance().close(fileWriter);
		}
	}

	private boolean deleteLockFile() throws BaseException {
		if (file.delete()) {
			return true;
		} else if (!file.isFile()) {
			return false;
		} else {
			throw new TechnicalException("Cannot delete the lock file: "
					+ file.getAbsolutePath());
		}

	}

	private void printWaitings(List<Entity> waitings) {
		LOG.info("Waiting for: ");
		for (Entity entity : waitings) {
			if (entity.getName().equals(file.getName())) {
				break;
			}
			LOG.info(entity.toString());
		}
	}

	private List<Entity> findEntities() throws BaseException {
		List<Entity> list = new ArrayList<LockerFile.Entity>();

		File[] files = dir.listFiles(fileFilterLock);
		Arrays.sort(files, comparatorFiles);

		boolean found = false;
		for (File file : files) {
			Entity entity = new Entity(file, file.getName(),
					file.lastModified());
			list.add(entity);
			if (file.getName().equals(this.file.getName())) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new TechnicalException(
					"I cannot find the lock I have created before: "
							+ file.getAbsolutePath());
		}

		return list;
	}
}
