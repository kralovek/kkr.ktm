package kkr.ktm.domains.common.components.diffmanager.filesystem.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.diffmanager.DiffManager;
import kkr.ktm.domains.common.components.diffmanager.data.DiffEntity;
import kkr.ktm.domains.common.components.diffmanager.data.DiffItem;
import kkr.ktm.domains.common.components.diffmanager.data.DiffStatus;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffEntityImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffIndexImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DiffItemImpl;
import kkr.ktm.domains.common.components.diffmanager.filesystem.data.DirInfo;

public class DiffManagerFilesystemLocal extends DiffManagerFilesystemLocalFwk implements DiffManager {
	private static final Logger LOG = Logger.getLogger(DiffManagerFilesystemLocal.class);

	private static final String UNIX_PATH_SEPARATOR = "/";

	private static Comparator<DiffItem> comparatorItem = new Comparator<DiffItem>() {
		public int compare(DiffItem item1, DiffItem item2) {
			return item1.getName().compareTo(item2.getName());
		}
	};

	public Collection<DiffEntity> loadDiffs(Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			for (DirInfo dirInfo : dirInfos) {
				DiffEntity groupState = findGroup(groupStates, dirInfo.getName());
				DiffEntity diffEntity = loadDiff(dirInfo, groupState);
				diffEntities.add(diffEntity);
			}

			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadDiff(DirInfo dirInfo, DiffEntity groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			long date = groupState != null ? ((DiffIndexImpl) groupState.getLastIndex()).getMs() : 0;

			List<DiffItem> diffItems = null;

			if (dirInfo.isContent() && groupState != null && groupState.getItems() != null) {
				List<DiffItemImpl> existingItems = getItems(dir, dir, 0, dirInfo.getPattern());

				diffItems = new ArrayList<DiffItem>();

				for (DiffItem itemState : groupState.getItems()) {
					DiffItemImpl existingItem = findItem(existingItems, itemState.getName());
					if (existingItem != null) {
						if (((DiffIndexImpl) existingItem.getIndex()).getMs() <= ((DiffIndexImpl) itemState.getIndex())
								.getMs()) {
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
				List<DiffItemImpl> existingItems = getItems(dir, dir, date, dirInfo.getPattern());
				diffItems = new ArrayList<DiffItem>();
				diffItems.addAll(existingItems);
			}

			long lastModified = getLastModified(diffItems);
			DiffEntityImpl group = new DiffEntityImpl(adaptEntryName(dirInfo.getName()));
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

	private static DiffItemImpl findItem(List<DiffItemImpl> items, String name) {
		for (DiffItemImpl item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	private DiffEntity findGroup(Collection<DiffEntity> diffEntities, String name) {
		name = adaptEntryName(name);
		if (diffEntities != null) {
			for (DiffEntity diffEntity : diffEntities) {
				if (name.equals(diffEntity.getName())) {
					return diffEntity;
				}
			}
		}
		return null;
	}

	public List<DiffEntity> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();

			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			for (DirInfo dirInfo : dirInfos) {
				if (dirInfo.isContent()) {
					DiffEntity diffEntity = loadDiff(dirInfo, null);
					diffEntities.add(diffEntity);
				} else {
					DiffEntity diffEntity = loadCurrent(dirInfo);
					diffEntities.add(diffEntity);
				}
			}

			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadCurrent(DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			File dir = new File(dirInfo.getPath());
			DiffEntityImpl group = new DiffEntityImpl(adaptEntryName(dirInfo.getName()));
			long lastModified = getLastModifiedDirectory(dir, dir, 0, dirInfo.getPattern());
			DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
			diffIndexImpl.setMs(lastModified);
			group.setLastIndex(diffIndexImpl);
			LOG.trace("OK");
			return group;
		} finally {
			LOG.trace("END");
		}
	}

	private static List<DiffItemImpl> getItems(File dirRoot, File dir, long index, Pattern pattern)
			throws BaseException {
		List<DiffItemImpl> items = new ArrayList<DiffItemImpl>();
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

				DiffItemImpl item = new DiffItemImpl(path);
				DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
				diffIndexImpl.setMs(file.lastModified());
				item.setIndex(diffIndexImpl);
				item.setStatus(DiffStatus.NEW);
				items.add(item);
			} else if (file.isDirectory()) {
				List<DiffItemImpl> itemsLocal = getItems(dirRoot, file, index, pattern);
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

	private static long getLastModifiedDirectory(File dirRoot, File dir, long lastModified, Pattern pattern)
			throws BaseException {
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
			if (lastModified > ((DiffIndexImpl) diffItem.getIndex()).getMs()) {
				lastModified = ((DiffIndexImpl) diffItem.getIndex()).getMs();
			}
		}
		return lastModified;
	}

	private String adaptEntryName(String name) {
		if (UtilsString.isEmpty(code)) {
			return name;
		} else {
			return code + "." + name;
		}
	}

	public String toString() {
		return "[" + code + "]: " + UtilsString.toStringCollection(dirInfos, null, null, ",");
	}
}
