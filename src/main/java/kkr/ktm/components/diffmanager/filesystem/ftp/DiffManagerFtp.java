package kkr.ktm.components.diffmanager.filesystem.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.filesystem.DirInfo;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.ftp.UtilsFtp;


public class DiffManagerFtp extends DiffManagerFtpFwk {
	private static final Logger LOG = Logger.getLogger(DiffManagerFtp.class);

	private static Comparator<Item> comparatorItem = new Comparator<Item>() {
		public int compare(Item item1, Item item2) {
			return item1.getName().compareTo(item2.getName());
		}
	};
	
	public List<Group> loadDiffs(List<Group> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<Group> groups = new ArrayList<Group>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return groups;
			}
			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin,
					ftpPassword);

			try {
				for (DirInfo dirInfo : dirInfos) {
					Group groupState = findGroup(groupStates, dirInfo.getName());
					Group group = loadDiff(client, dirInfo, groupState);
					groups.add(group);
				}
			} finally {
				try {
					client.disconnect();
				} catch (IOException ex) {
					// rien � faire
				}
			}
			LOG.trace("OK");
			return groups;
		} finally {
			LOG.trace("END");
		}
	}

	private Group loadDiff(FTPClient client, DirInfo dirInfo,
			Group groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			long date = groupState != null ? ((IndexImpl) groupState.getLastIndex()).getMs()
					: 0;
			List<Item> items = null;
			if (dirInfo.isContent() && groupState != null
					&& groupState.getItems() != null) {
				List<ItemImpl> existingItems = getItems(client,
						dirInfo.getPath(), dirInfo.getPath(), 0,
						dirInfo.getPattern());
				
				items = new ArrayList<Item>();

				for (Item itemState : groupState.getItems()) {
					ItemImpl existingItem = findItem(existingItems,
							itemState.getName());
					if (existingItem != null) {
						if (((IndexImpl) existingItem.getIndex()).getMs() <= ((IndexImpl) itemState.getIndex()).getMs()) {
							existingItems.remove(existingItem);
						} else {
							existingItem.setStatus(Status.UPD);
						}
					} else {
						ItemImpl newItem = new ItemImpl(itemState);
						newItem.setStatus(Status.DEL);
						items.add(newItem);
					}
				}
				items.addAll(existingItems);
				Collections.sort(items, comparatorItem); 
			} else {
				List<ItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(),
						date, dirInfo.getPattern());
				items = new ArrayList<Item>();
				items.addAll(existingItems);
			}

			long lastModified = getLastModified(items);
			GroupImpl group = new GroupImpl(dirInfo.getName());
			IndexImpl indexImpl = new IndexImpl();
			indexImpl.setMs(lastModified);
			group.setLastIndex(indexImpl);
			group.getItems().addAll(items);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private ItemImpl findItem(List<ItemImpl> items, String name) {
		for (ItemImpl item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	
	public List<Group> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<Group> groups = new ArrayList<Group>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return groups;
			}
			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin,
					ftpPassword);

			try {
				for (DirInfo dirInfo : dirInfos) {
					if (dirInfo.isContent()) {
						Group group = loadDiff(client, dirInfo, null);
						groups.add(group);
					} else {
						Group group = loadCurrent(client, dirInfo);
						groups.add(group);
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
			return groups;
		} finally {
			LOG.trace("END");
		}
	}

	private Group loadCurrent(FTPClient client, DirInfo dirInfo)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			long lastModified = getLastModifiedDirectory(client,
					dirInfo.getPath(), dirInfo.getPath(), 0,
					dirInfo.getPattern());
			GroupImpl group = new GroupImpl(dirInfo.getName());
			IndexImpl indexImpl = new IndexImpl();
			indexImpl.setMs(lastModified);
			group.setLastIndex(indexImpl);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private static List<ItemImpl> getItems(FTPClient client, String dirRoot,
			String dir, long index, Pattern pattern) throws BaseException {
		List<ItemImpl> items = new ArrayList<ItemImpl>();

		FTPFile[] files = null;
		try {
			files = client.listFiles(dir);
		} catch (IOException ex) {
			throw new TechnicalException(
					"Cannot read the content of the directory: " + dir, ex);
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

				ItemImpl item = new ItemImpl(path);
				IndexImpl indexImpl = new IndexImpl();
				indexImpl.setMs(file.getTimestamp().getTimeInMillis());
				item.setIndex(indexImpl);
				item.setStatus(Status.NEW);
				items.add(item);
			} else if (file.isDirectory()) {
				String path = dir + file.getName() + PATH_SEPARATOR;
				List<ItemImpl> itemsLocal = getItems(client, dirRoot, path,
						index, pattern);
				items.addAll(itemsLocal);
			} else if (file.isSymbolicLink()) {
				// TODO
			}
		}

		Collections.sort(items, comparatorItem);
		
		return items;
	}

	private static Group findGroup(List<Group> groups, String name) {
		if (groups != null) {
			for (Group group : groups) {
				if (name.equals(group.getName())) {
					return group;
				}
			}
		}
		return null;
	}

	private static long getLastModifiedDirectory(FTPClient client,
			String dirRoot, String dir, long lastModified, Pattern pattern)
			throws BaseException {

		FTPFile[] files = null;
		try {
			files = client.listFiles(dir);
		} catch (IOException ex) {
			throw new TechnicalException(
					"Cannot read the content of the directory: " + dir, ex);
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
				lastModified = lastModified >= file.getTimestamp()
						.getTimeInMillis() ? lastModified : file.getTimestamp()
						.getTimeInMillis();
			} else if (file.isDirectory()) {
				String path = dir + file.getName() + PATH_SEPARATOR;
				long lastModifiedLocal = getLastModifiedDirectory(client,
						dirRoot, path, lastModified, pattern);
				lastModified = lastModified >= lastModifiedLocal ? lastModified
						: lastModifiedLocal;
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
		relativePath = relativePath
				.replace(PATH_SEPARATOR, UNIX_PATH_SEPARATOR);
		return relativePath;
	}

	private static long getLastModified(List<Item> items) {
		long lastModified = 0;
		for (Item item : items) {
			// To je divny ... ta podminka ma byt naopak !!!
			if (lastModified > ((IndexImpl) item.getIndex()).getMs()) {
				lastModified = ((IndexImpl) item.getIndex()).getMs();
			}
		}
		return lastModified;
	}
}
