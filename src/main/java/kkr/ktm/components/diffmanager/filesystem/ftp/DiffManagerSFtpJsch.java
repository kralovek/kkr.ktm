package kkr.ktm.components.diffmanager.filesystem.ftp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import kkr.ktm.components.diffmanager.data.DiffGroup;
import kkr.ktm.components.diffmanager.data.DiffItem;
import kkr.ktm.components.diffmanager.data.DiffStatus;
import kkr.ktm.components.diffmanager.filesystem.DirInfo;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;

public class DiffManagerSFtpJsch extends DiffManagerFtpFwk {
	private static final String UNIX_PATH_SEPARATOR = "/";

	private static final Logger LOG = Logger.getLogger(DiffManagerSFtpJsch.class);

	private static Comparator<DiffItem> comparatorItem = new Comparator<DiffItem>() {
		public int compare(DiffItem item1, DiffItem item2) {
			return item1.getName().compareTo(item2.getName());
		}
	};

	private static class Client {
		private Session session;
		private ChannelSftp channel;

		public Session getSession() {
			return session;
		}

		public void setSession(Session session) {
			this.session = session;
		}

		public ChannelSftp getChannel() {
			return channel;
		}

		public void setChannel(ChannelSftp channel) {
			this.channel = channel;
		}
	}

	public List<DiffGroup> loadDiffs(List<DiffGroup> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffGroups;
			}
			Client client = connect();

			try {
				for (DirInfo dirInfo : dirInfos) {
					DiffGroup groupState = findGroup(groupStates, dirInfo.getName());
					DiffGroup diffGroup = loadDiff(client, dirInfo, groupState);
					diffGroups.add(diffGroup);
				}
			} finally {
				disconnect(client);
			}
			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup loadDiff(Client client, DirInfo dirInfo, DiffGroup groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.debug("dir: " + dirInfo.getPath());
			long date = groupState != null ? ((IndexImpl) groupState.getLastIndex()).getMs() : 0;
			List<DiffItem> diffItems = null;
			if (dirInfo.isContent() && groupState != null && groupState.getItems() != null) {
				List<ItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), 0, dirInfo.getPattern());

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
				List<ItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), date, dirInfo.getPattern());
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

	private ItemImpl findItem(List<ItemImpl> items, String name) {
		for (ItemImpl item : items) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
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

	private static List<ItemImpl> getItems(Client client, String dirRoot, String dir, long index, Pattern pattern) throws BaseException {
		List<ItemImpl> items = new ArrayList<ItemImpl>();

		Vector<LsEntry> files = null;
		try {
			files = client.getChannel().ls(dir);
		} catch (SftpException ex) {
			throw new TechnicalException("Cannot read the content of the directory: " + dir, ex);
		}

		if (files == null) {
			return items;
		}

		for (LsEntry file : files) {
			if (!file.getAttrs().isDir()) {
				long ms = 1000L * (long) file.getAttrs().getATime();
				if (ms <= index) {
					// Ignor old files
					continue;
				}
				String path = adaptPath(dirRoot, dir + file.getFilename());

				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}

				ItemImpl item = new ItemImpl(path);
				IndexImpl indexImpl = new IndexImpl();
				indexImpl.setMs(ms);
				item.setIndex(indexImpl);
				item.setStatus(DiffStatus.NEW);
				items.add(item);
			} else if (file.getAttrs().isDir()) {
				if (".".equals(file.getFilename()) || "..".equals(file.getFilename())) {
					continue;
				}
				String path = dir + file.getFilename() + PATH_SEPARATOR;
				List<ItemImpl> itemsLocal = getItems(client, dirRoot, path, index, pattern);
				items.addAll(itemsLocal);
			}
		}

		Collections.sort(items, comparatorItem);

		return items;
	}

	public List<DiffGroup> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			LOG.debug("diff: " + name);
			List<DiffGroup> diffGroups = new ArrayList<DiffGroup>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffGroups;
			}
			Client client = connect();

			try {
				for (DirInfo dirInfo : dirInfos) {
					if (dirInfo.isContent()) {
						DiffGroup diffGroup = loadDiff(client, dirInfo, null);
						diffGroups.add(diffGroup);
					} else {
						DiffGroup diffGroup = loadCurrent(client, dirInfo);
						diffGroups.add(diffGroup);
					}
				}
			} finally {
				disconnect(client);
			}
			LOG.trace("OK");
			return diffGroups;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffGroup loadCurrent(Client client, DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			long lastModified = getLastModifiedDirectory(client, dirInfo.getPath(), dirInfo.getPath(), 0, dirInfo.getPattern());
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

	private static long getLastModifiedDirectory(Client client, String dirRoot, String dir, long lastModified, Pattern pattern) throws BaseException {

		Vector<LsEntry> files = null;
		try {
			files = client.getChannel().ls(dir);
		} catch (SftpException ex) {
			throw new TechnicalException("Cannot read the content of the directory: " + dir, ex);
		}

		if (files == null) {
			return 0;
		}

		for (LsEntry file : files) {
			if (!file.getAttrs().isDir()) {
				LOG.debug("file: " + file.getFilename());

				String path = adaptPath(dirRoot, dir + file.getFilename());
				if (pattern != null && !pattern.matcher(path).matches()) {
					LOG.debug("Ignored file: " + file);
					continue;
				}
				long ms = 1000L * (long) file.getAttrs().getATime();
				lastModified = lastModified >= ms ? lastModified : ms;
			} else if (file.getAttrs().isDir()) {
				String path = dir + file.getFilename() + PATH_SEPARATOR;
				long lastModifiedLocal = getLastModifiedDirectory(client, dirRoot, path, lastModified, pattern);
				lastModified = lastModified >= lastModifiedLocal ? lastModified : lastModifiedLocal;
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

	private Client connect() throws BaseException {
		Session session = null;
		ChannelSftp sftpChannel = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(ftpLogin, ftpHost, ftpPort);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(ftpPassword);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

			Client client = new Client();
			client.setSession(session);
			client.setChannel(sftpChannel);
			return client;
		} catch (JSchException ex) {
			throw new TechnicalException("SFTP Cannot connect to the server: " + ftpHost + ":" + ftpPort, ex);
		}
	}

	private void disconnect(Client client) {
		if (client == null) {
			return;
		}
		if (client.getChannel() != null) {
			client.getChannel().disconnect();
		}
		if (client.getSession() != null) {
			client.getSession().disconnect();
		}
	}
}
