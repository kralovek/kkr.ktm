package kkr.ktm.components.cleanmanager.filesystem.local;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.ktm.components.cleanmanager.CleanManager;
import kkr.ktm.components.cleanmanager.filesystem.DirInfo;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TechnicalException;

public class CleanManagerFilesystem extends CleanManagerFilesystemFwk
		implements CleanManager {
	private static final Logger LOG = Logger
			.getLogger(CleanManagerFilesystem.class);

	private static class FileFilterBase {
		private String rootPath;
		private List<Pattern> patterns;

		public FileFilterBase(File rootDir, List<Pattern> patterns) {
			this.patterns = patterns;
			if (rootDir != null) {
				rootPath = canonicalDir(rootDir);
			}
		}

		private String canonicalDir(File file) {
			String path;
			try {
				path = file.getCanonicalPath();
			} catch (IOException ex) {
				path = file.getAbsolutePath();
			}
			path = path.replace(File.separator, "/");
			if (file.isDirectory() && !path.endsWith("/")) {
				path += "/";
			}
			return path;
		}

		private String canonicalFile(File file) {
			String path;
			try {
				path = file.getCanonicalPath();
			} catch (IOException ex) {
				path = file.getAbsolutePath();
			}
			path = path.replace(File.separator, "/");
			return path;
		}

		private String relativeFilePath(File file) {
			String filePath = canonicalFile(file);
			if (filePath.equals(rootPath)) {
				return null;
			}
			if (rootPath != null && filePath.startsWith(rootPath)) {
				filePath = filePath.substring(rootPath.length());
			}
			return filePath;
		}

		protected boolean matches(File file) {
			if (patterns == null) {
				return false;
			}
			String fileRelativePath = relativeFilePath(file);
			if (fileRelativePath == null) {
				return false;
			}
			for (Pattern pattern : patterns) {
				if (pattern.matcher(fileRelativePath).matches()) {
					return true;
				}
			}
			return false;
		}
	}

	private static class FileFilterMatchingFile extends FileFilterBase
			implements FileFilter {
		public FileFilterMatchingFile(File root, List<Pattern> patterns) {
			super(root, patterns);
		}

		public boolean accept(File file) {
			return file.isFile() && matches(file);
		}
	}

	private static class FileFilterMatchingDir extends FileFilterBase implements
			FileFilter {
		public FileFilterMatchingDir(File root, List<Pattern> patterns) {
			super(root, patterns);
		}

		public boolean accept(File file) {
			return file.isDirectory() && matches(file);
		}
	}

	private static class FileFilterNonMatchingDir extends FileFilterBase
			implements FileFilter {
		public FileFilterNonMatchingDir(File root, List<Pattern> patterns) {
			super(root, patterns);
		}

		public boolean accept(File file) {
			return file.isDirectory() && !matches(file);
		}
	}

	public void clean(List<Group> groups) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			if (groups == null || groups.isEmpty()) {
				LOG.warn("No group to remove");
			}
			String totalNonRemoved = "";
			for (Group group : groups) {
				DirInfo dirInfo = findDirInfo(group.getName());
				if (dirInfo == null) {
					throw new ConfigurationException(
							"The directory for the group is not configured: "
									+ group.getName());
				}

				Set<File> nonRemoved = removeFiles(dirInfo.getPath(),
						group.getPatterns());
				if (!nonRemoved.isEmpty()) {
					totalNonRemoved += toStringNonRemoved(dirInfo.getPath(),
							nonRemoved) + "\n";
				}
			}

			if (!totalNonRemoved.isEmpty()) {
				throw new TechnicalException("Cannot remove some files: "
						+ totalNonRemoved);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private String toStringNonRemoved(String rootPath, Set<File> nonRemoved) {
		StringBuffer buffer = new StringBuffer();
		for (File file : nonRemoved) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}
			buffer.append(file.getAbsolutePath());
		}
		return "[" + rootPath + "] " + buffer.toString();
	}

	private DirInfo findDirInfo(String name) {
		for (DirInfo dirInfo : dirInfos) {
			if (dirInfo.getName().equals(name)) {
				return dirInfo;
			}
		}
		return null;
	}

	public Set<File> removeFiles(String path, List<String> strPatterns)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<Pattern> patterns = new ArrayList<Pattern>();
			for (String strPattern : strPatterns) {
				Pattern pattern = Pattern.compile(strPattern);
				patterns.add(pattern);
			}

			File dirRoot = new File(path);

			FileFilterMatchingFile fileFilterMatchingFile = new FileFilterMatchingFile(
					dirRoot, patterns);
			FileFilterMatchingDir fileFilterMatchingDir = new FileFilterMatchingDir(
					dirRoot, patterns);
			FileFilterNonMatchingDir fileFilterNonMatchingDir = new FileFilterNonMatchingDir(
					dirRoot, patterns);

			Set<File> nonRemoved = workDirRemoveFiles(dirRoot,
					fileFilterMatchingFile, fileFilterMatchingDir,
					fileFilterNonMatchingDir);

			LOG.trace("OK");
			return nonRemoved;
		} finally {
			LOG.trace("END");
		}
	}

	private Set<File> workDirRemoveFiles(File dir, //
			FileFilter fileFilterMatchingFile, //
			FileFilter fileFilterMatchingDir, //
			FileFilter fileFilterNonMatchingDir) {
		Set<File> notRemoved = new LinkedHashSet<File>();

		//
		// Files to remove
		//
		File[] filesToRemove = dir.listFiles(fileFilterMatchingFile);
		for (File fileToRemove : filesToRemove) {
			if (!fileToRemove.delete()) {
				notRemoved.add(fileToRemove);
			}
		}

		//
		// Dirs to remove
		//
		File[] dirsToRemove = dir.listFiles(fileFilterMatchingDir);
		for (File dirToRemove : dirsToRemove) {
			Set<File> notRemovedLoc = removeNonemptyDir(dirToRemove);
			notRemoved.addAll(notRemovedLoc);
		}

		//
		// Dirs to passe
		//
		File[] dirs = dir.listFiles(fileFilterNonMatchingDir);
		for (File dirLoc : dirs) {
			Set<File> notRemovedLoc = workDirRemoveFiles(dirLoc,
					fileFilterMatchingFile, fileFilterMatchingDir,
					fileFilterNonMatchingDir);
			notRemoved.addAll(notRemovedLoc);
		}

		return notRemoved;
	}

	private Set<File> removeNonemptyDir(File dir) {
		Set<File> nonRemoved = new LinkedHashSet<File>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					nonRemoved.add(file);
				}
			} else if (file.isDirectory()) {
				Set<File> nonRemovedLoc = removeNonemptyDir(file);
				nonRemoved.addAll(nonRemovedLoc);
				if (!file.delete()) {
					nonRemoved.add(file);
				}
			}
		}
		if (!dir.delete()) {
			nonRemoved.add(dir);
		}
		return nonRemoved;
	}

	public static final void main(String[] argv) throws Exception {
		CleanManagerFilesystem fileManagerLocal = new CleanManagerFilesystem();
		fileManagerLocal.config();

		List<String> patterns = new ArrayList<String>();

		patterns.add("config");
		patterns.add("trace/COWD_CONFIG/INT");
		patterns.add("trace/COWD_CONFIG/LOC/.*\\.gz");
		patterns.add("run.*COWD_CONFIG.*");

		fileManagerLocal.removeFiles("d:\\tmp\\52\\root", patterns);
	}
}
