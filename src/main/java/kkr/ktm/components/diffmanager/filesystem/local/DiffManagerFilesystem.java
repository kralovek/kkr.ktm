package kkr.ktm.components.diffmanager.filesystem.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.components.diffmanager.filesystem.DirInfo;
import kkr.ktm.exception.BaseException;

public class DiffManagerFilesystem extends DiffManagerFilesystemFwk implements
		DiffManager {
	private static final Logger LOG = Logger
			.getLogger(DiffManagerFilesystem.class);

	private static final String UNIX_PATH_SEPARATOR = "/";

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

			for (DirInfo dirInfo : dirInfos) {
				Group groupState = findGroup(groupStates, dirInfo.getName());
				Group group = loadDiff(dirInfo, groupState);
				groups.add(group);
			}

			LOG.trace("OK");
			return groups;
		} finally {
			LOG.trace("END");
		}
	}

	private Group loadDiff(DirInfo dirInfo, Group groupState)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			long date = groupState != null ? ((IndexImpl) groupState
					.getLastIndex()).getMs() : 0;

			List<Item> items = null;

			if (dirInfo.isContent() && groupState != null
					&& groupState.getItems() != null) {
				List<ItemImpl> existingItems = getItems(dir, dir, 0,
						dirInfo.getPattern());

				items = new ArrayList<Item>();

				for (Item itemState : groupState.getItems()) {
					ItemImpl existingItem = findItem(existingItems,
							itemState.getName());
					if (existingItem != null) {
						if (((IndexImpl) existingItem.getIndex()).getMs() <= ((IndexImpl) itemState
								.getIndex()).getMs()) {
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
				List<ItemImpl> existingItems = getItems(dir, dir, date,
						dirInfo.getPattern());
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

	private static ItemImpl findItem(List<ItemImpl> items, String name) {
		for (ItemImpl item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
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

	public List<Group> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			List<Group> groups = new ArrayList<Group>();

			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.isContent()) {
					Group group = loadDiff(dirInfo, null);
					groups.add(group);
				} else {
					Group group = loadCurrent(dirInfo);
					groups.add(group);
				}
			}

			LOG.trace("OK");
			return groups;
		} finally {
			LOG.trace("END");
		}
	}

	public Group loadCurrent(DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			GroupImpl group = new GroupImpl(dirInfo.getName());
			long lastModified = getLastModifiedDirectory(dir, dir, 0,
					dirInfo.getPattern());
			IndexImpl indexImpl = new IndexImpl();
			indexImpl.setMs(lastModified);
			group.setLastIndex(indexImpl);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private static List<ItemImpl> getItems(File dirRoot, File dir, long index,
			Pattern pattern) throws BaseException {
		List<ItemImpl> items = new ArrayList<ItemImpl>();
		File[] files = dir.listFiles();

		if (files == null) {
			return items;
		}

		for (File file : files) {
			if (file.isFile()) {
				if (file.lastModified() <= index) {
					// Ignor old files
					continue;
				}
				String path = adaptPath(dirRoot, file);

				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}

				ItemImpl item = new ItemImpl(path);
				IndexImpl indexImpl = new IndexImpl();
				indexImpl.setMs(file.lastModified());
				item.setIndex(indexImpl);
				item.setStatus(Status.NEW);
				items.add(item);
			} else if (file.isDirectory()) {
				List<ItemImpl> itemsLocal = getItems(dirRoot, file, index,
						pattern);
				items.addAll(itemsLocal);
			}
		}

		Collections.sort(items, comparatorItem);

		return items;
	}

	private static String adaptPath(File dir, File file) {
		String relativePath = file.getAbsolutePath().substring(
				dir.getAbsolutePath().length());
		if (relativePath.startsWith(File.separator)) {
			relativePath = relativePath.substring(File.separator.length());
		}
		relativePath = relativePath
				.replace(File.separator, UNIX_PATH_SEPARATOR);
		return relativePath;
	}

	private static long getLastModifiedDirectory(File dirRoot, File dir,
			long lastModified, Pattern pattern) throws BaseException {
		File[] files = dir.listFiles();

		if (files == null) {
			return 0;
		}

		for (File file : files) {
			if (file.isFile()) {
				String path = adaptPath(dirRoot, file);

				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}

				lastModified = lastModified >= file.lastModified() ? lastModified
						: file.lastModified();
			} else if (file.isDirectory()) {
				long lastModifiedLocal = getLastModifiedDirectory(dirRoot,
						file, lastModified, pattern);
				lastModified = lastModified >= lastModifiedLocal ? lastModified
						: lastModifiedLocal;
			}
		}

		return lastModified;
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
