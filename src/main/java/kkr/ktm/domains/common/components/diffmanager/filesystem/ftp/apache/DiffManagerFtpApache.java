package kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.apache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.TechnicalException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffEntityImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffIndexImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffItemImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DirInfo;
import kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.base.DiffManagerFtpBaseFwk;
import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.utils.ftp.UtilsFtp;

public class DiffManagerFtpApache extends DiffManagerFtpBaseFwk {
	private static final Logger LOG = Logger.getLogger(DiffManagerFtpApache.class);

	private static Comparator<DiffItem> comparatorItem = new Comparator<DiffItem>() {
		public int compare(DiffItem item1, DiffItem item2) {
			return item1.getName().compareTo(item2.getName());
		}
	};

	public Collection<DiffEntity> loadDiffs(Test test, Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Collection<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffEntities;
			}
			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin, ftpPassword);

			try {
				for (DirInfo dirInfo : dirInfos) {
					DiffEntity groupState = findGroup(groupStates, dirInfo.getName());
					DiffEntity diffEntity = loadDiff(client, dirInfo, groupState);
					diffEntities.add(diffEntity);
				}
			} finally {
				try {
					client.disconnect();
				} catch (IOException ex) {
					// nothing to do
				}
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadDiff(FTPClient client, DirInfo dirInfo, DiffEntity groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			long date = groupState != null ? ((DiffIndexImpl) groupState.getLastIndex()).getMs() : 0;
			List<DiffItem> diffItems = null;
			if (dirInfo.isContent() && groupState != null && groupState.getItems() != null) {
				Collection<DiffItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), 0, dirInfo.getPattern());

				diffItems = new ArrayList<DiffItem>();

				for (DiffItem itemState : groupState.getItems()) {
					DiffItemImpl existingItem = findItem(existingItems, itemState.getName());
					if (existingItem != null) {
						if (((DiffIndexImpl) existingItem.getIndex()).getMs() <= ((DiffIndexImpl) itemState.getIndex()).getMs()) {
							existingItems.remove(existingItem);
						} else {
							existingItem.setStatus(DiffStatus.UPD);
						}
					} else {
						DiffItemImpl newItem = new DiffItemImpl(itemState);
						newItem.setStatus(DiffStatus.DEL);
						diffItems.add(newItem);
					}
				}
				diffItems.addAll(existingItems);
				Collections.sort(diffItems, comparatorItem);
			} else {
				List<DiffItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), date, dirInfo.getPattern());
				diffItems = new ArrayList<DiffItem>();
				diffItems.addAll(existingItems);
			}

			long lastModified = getLastModified(diffItems);
			DiffEntityImpl group = new DiffEntityImpl(adaptEntityName(dirInfo.getName()));
			DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
			diffIndexImpl.setMs(lastModified);
			group.setLastIndex(diffIndexImpl);
			group.getItems().addAll(diffItems);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffItemImpl findItem(Collection<DiffItemImpl> items, String name) {
		for (DiffItemImpl item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	public List<DiffEntity> loadCurrents(Test test) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffEntities;
			}
			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin, ftpPassword);

			try {
				for (DirInfo dirInfo : dirInfos) {
					if (dirInfo.isContent()) {
						DiffEntity diffEntity = loadDiff(client, dirInfo, null);
						diffEntities.add(diffEntity);
					} else {
						DiffEntity diffEntity = loadCurrent(client, dirInfo);
						diffEntities.add(diffEntity);
					}
				}
			} finally {
				try {
					client.disconnect();
				} catch (IOException ex) {
					// rien � faire
				}
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadCurrent(FTPClient client, DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			long lastModified = getLastModifiedDirectory(client, dirInfo.getPath(), dirInfo.getPath(), 0, dirInfo.getPattern());
			DiffEntityImpl group = new DiffEntityImpl(adaptEntityName(dirInfo.getName()));
			DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
			diffIndexImpl.setMs(lastModified);
			group.setLastIndex(diffIndexImpl);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private static List<DiffItemImpl> getItems(FTPClient client, String dirRoot, String dir, long index, Pattern pattern) throws BaseException {
		List<DiffItemImpl> items = new ArrayList<DiffItemImpl>();

		FTPFile[] files = null;
		try {
			files = client.listFiles(dir);
		} catch (IOException ex) {
			throw new TechnicalException("Cannot read the content of the directory: " + dir, ex);
		}

		if (files == null) {
			return items;
		}

		for (FTPFile file : files) {
			if (file.isFile()) {
				if (file.getTimestamp().getTimeInMillis() <= index) {
					// Ignor old files
					continue;
				}
				String path = adaptPath(dirRoot, dir + file.getName());

				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}

				DiffItemImpl item = new DiffItemImpl(path);
				DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
				diffIndexImpl.setMs(file.getTimestamp().getTimeInMillis());
				item.setIndex(diffIndexImpl);
				item.setStatus(DiffStatus.NEW);
				items.add(item);
			} else if (file.isDirectory()) {
				String path = dir + file.getName() + PATH_SEPARATOR;
				List<DiffItemImpl> itemsLocal = getItems(client, dirRoot, path, index, pattern);
				items.addAll(itemsLocal);
			} else if (file.isSymbolicLink()) {
				// TODO
			}
		}

		Collections.sort(items, comparatorItem);

		return items;
	}

	private DiffEntity findGroup(Collection<DiffEntity> diffEntities, String name) {
		name = adaptEntityName(name);
		if (diffEntities != null) {
			for (DiffEntity diffEntity : diffEntities) {
				if (name.equals(diffEntity.getName())) {
					return diffEntity;
				}
			}
		}
		return null;
	}

	private static long getLastModifiedDirectory(FTPClient client, String dirRoot, String dir, long lastModified, Pattern pattern)
			throws BaseException {

		FTPFile[] files = null;
		try {
			files = client.listFiles(dir);
		} catch (IOException ex) {
			throw new TechnicalException("Cannot read the content of the directory: " + dir, ex);
		}

		if (files == null) {
			return 0;
		}

		for (FTPFile file : files) {
			if (file.isFile()) {
				LOG.debug("file: " + file.getName());

				String path = adaptPath(dirRoot, dir + file.getName());
				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}
				lastModified = lastModified >= file.getTimestamp().getTimeInMillis() ? lastModified : file.getTimestamp().getTimeInMillis();
			} else if (file.isDirectory()) {
				String path = dir + file.getName() + PATH_SEPARATOR;
				long lastModifiedLocal = getLastModifiedDirectory(client, dirRoot, path, lastModified, pattern);
				lastModified = lastModified >= lastModifiedLocal ? lastModified : lastModifiedLocal;
			} else if (file.isSymbolicLink()) {
				// TODO
			}
		}

		return lastModified;
	}

	private static String adaptPath(String dir, String file) {
		String relativePath = file.substring(dir.length());
		if (relativePath.startsWith(PATH_SEPARATOR)) {
			relativePath = relativePath.substring(PATH_SEPARATOR.length());
		}
		relativePath = relativePath.replace(PATH_SEPARATOR, UNIX_PATH_SEPARATOR);
		return relativePath;
	}

	private static long getLastModified(Collection<DiffItem> diffItems) {
		long lastModified = 0;
		for (DiffItem diffItem : diffItems) {
			// To je divny ... ta podminka ma byt naopak !!!
			if (lastModified > ((DiffIndexImpl) diffItem.getIndex()).getMs()) {
				lastModified = ((DiffIndexImpl) diffItem.getIndex()).getMs();
			}
		}
		return lastModified;
	}

	private String adaptEntityName(String name) {
		if (UtilsString.isEmpty(code)) {
			return name;
		} else {
			return code + "." + name;
		}
	}
}
