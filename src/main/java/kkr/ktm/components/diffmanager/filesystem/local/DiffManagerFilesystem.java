package kkr.ktm.components.diffmanager.filesystem.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.ktm.components.diffmanager.DiffManager;
import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;
import kkr.ktm.components.diffmanager.filesystem.DirInfo;
import kkr.ktm.exception.BaseException;

public class DiffManagerFilesystem extends DiffManagerFilesystemFwk implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerFilesystem.class);

	private static final String UNIX_PATH_SEPARATOR = "/";

	private static Comparator<DiffItem> comparatorItem = new Comparator<DiffItem>() {
		public int compare(DiffItem item1, DiffItem item2) {
			return item1.getName().compareTo(item2.getName());
		}
	};

	public List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			for (DirInfo dirInfo : dirInfos) {
				DiffGroup groupState = findGroup(groupStates, dirInfo.getName());
				DiffGroup diffGroup = loadDiff(dirInfo, groupState);
				diffGroups.add(diffGroup);
			}

			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup loadDiff(DirInfo dirInfo, DiffGroup groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			long date = groupState != null ? ((IndexImpl) groupState.getLastIndex()).getMs() : 0;

			List<DiffItem> diffItems = null;

			if (dirInfo.isContent() && groupState != null && groupState.getItems() != null) {
				List<ItemImpl> existingItems = getItems(dir, dir, 0, dirInfo.getPattern());

				diffItems = new ArrayList<DiffItem>();

				for (DiffItem itemState : groupState.getItems()) {
					ItemImpl existingItem = findItem(existingItems, itemState.getName());
					if (existingItem != null) {
						if (((IndexImpl) existingItem.getIndex()).getMs() <= ((IndexImpl) itemState.getIndex()).getMs()) {
							existingItems.remove(existingItem);
						} else {
							existingItem.setStatus(DiffStatus.UPD);
						}
					} else {
						ItemImpl newItem = new ItemImpl(itemState);
						newItem.setStatus(DiffStatus.DEL);
						diffItems.add(newItem);
					}
				}

				diffItems.addAll(existingItems);
				Collections.sort(diffItems, comparatorItem);
			} else {
				List<ItemImpl> existingItems = getItems(dir, dir, date, dirInfo.getPattern());
				diffItems = new ArrayList<DiffItem>();
				diffItems.addAll(existingItems);
			}

			long lastModified = getLastModified(diffItems);
			GroupImpl group = new GroupImpl(dirInfo.getName());
			IndexImpl indexImpl = new IndexImpl();
			indexImpl.setMs(lastModified);
			group.setLastIndex(indexImpl);
			group.getItems().addAll(diffItems);
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

	private static DiffGroup findGroup(List<DiffGroup> diffGroups, String name) {
		if (diffGroups != null) {
			for (DiffGroup diffGroup : diffGroups) {
				if (name.equals(diffGroup.getName())) {
					return diffGroup;
				}
			}
		}
		return null;
	}

	public List<DiffGroup> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.isContent()) {
					DiffGroup diffGroup = loadDiff(dirInfo, null);
					diffGroups.add(diffGroup);
				} else {
					DiffGroup diffGroup = loadCurrent(dirInfo);
					diffGroups.add(diffGroup);
				}
			}

			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	public DiffGroup loadCurrent(DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			GroupImpl group = new GroupImpl(dirInfo.getName());
			long lastModified = getLastModifiedDirectory(dir, dir, 0, dirInfo.getPattern());
			IndexImpl indexImpl = new IndexImpl();
			indexImpl.setMs(lastModified);
			group.setLastIndex(indexImpl);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private static List<ItemImpl> getItems(File dirRoot, File dir, long index, Pattern pattern) throws BaseException {
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
				item.setStatus(DiffStatus.NEW);
				items.add(item);
			} else if (file.isDirectory()) {
				List<ItemImpl> itemsLocal = getItems(dirRoot, file, index, pattern);
				items.addAll(itemsLocal);
			}
		}

		Collections.sort(items, comparatorItem);

		return items;
	}

	private static String adaptPath(File dir, File file) {
		String relativePath = file.getAbsolutePath().substring(dir.getAbsolutePath().length());
		if (relativePath.startsWith(File.separator)) {
			relativePath = relativePath.substring(File.separator.length());
		}
		relativePath = relativePath.replace(File.separator, UNIX_PATH_SEPARATOR);
		return relativePath;
	}

	private static long getLastModifiedDirectory(File dirRoot, File dir, long lastModified, Pattern pattern) throws BaseException {
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

				lastModified = lastModified >= file.lastModified() ? lastModified : file.lastModified();
			} else if (file.isDirectory()) {
				long lastModifiedLocal = getLastModifiedDirectory(dirRoot, file, lastModified, pattern);
				lastModified = lastModified >= lastModifiedLocal ? lastModified : lastModifiedLocal;
			}
		}

		return lastModified;
	}

	private static long getLastModified(List<DiffItem> diffItems) {
		long lastModified = 0;
		for (DiffItem diffItem : diffItems) {
			// To je divny ... ta podminka ma byt naopak !!!
			if (lastModified > ((IndexImpl) diffItem.getIndex()).getMs()) {
				lastModified = ((IndexImpl) diffItem.getIndex()).getMs();
			}
		}
		return lastModified;
	}
}
