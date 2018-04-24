package kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.jsch;

import java.util.ArrayList;
import java.util.Collection;
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
import kkr.ktm.domains.common.components.diffmanager.filesystem.ftp.base.DiffManagerFtpBase;

public class DiffManagerSFtpJsch extends DiffManagerFtpBase {
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

	public Collection<DiffEntity> loadDiffs(Collection<DiffEntity> groupStates) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffEntities;
			}
			Client client = connect();

			try {
				for (DirInfo dirInfo : dirInfos) {
					DiffEntity groupState = findGroup(groupStates, dirInfo.getName());
					DiffEntity diffEntity = loadDiff(client, dirInfo, groupState);
					diffEntities.add(diffEntity);
				}
			} finally {
				disconnect(client);
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadDiff(Client client, DirInfo dirInfo, DiffEntity groupState) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.debug("dir: " + dirInfo.getPath());
			long date = groupState != null ? ((DiffIndexImpl) groupState.getLastIndex()).getMs() : 0;
			List<DiffItem> diffItems = null;
			if (dirInfo.isContent() && groupState != null && groupState.getItems() != null) {
				List<DiffItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), 0,
						dirInfo.getPattern());

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
				List<DiffItemImpl> existingItems = getItems(client, dirInfo.getPath(), dirInfo.getPath(), date,
						dirInfo.getPattern());
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

	private DiffItemImpl findItem(List<DiffItemImpl> items, String name) {
		for (DiffItemImpl item : items) {
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
			if (lastModified > ((DiffIndexImpl) diffItem.getIndex()).getMs()) {
				lastModified = ((DiffIndexImpl) diffItem.getIndex()).getMs();
			}
		}
		return lastModified;
	}

	private static List<DiffItemImpl> getItems(Client client, String dirRoot, String dir, long index, Pattern pattern)
			throws BaseException {
		List<DiffItemImpl> items = new ArrayList<DiffItemImpl>();

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

				DiffItemImpl item = new DiffItemImpl(path);
				DiffIndexImpl diffIndexImpl = new DiffIndexImpl();
				diffIndexImpl.setMs(ms);
				item.setIndex(diffIndexImpl);
				item.setStatus(DiffStatus.NEW);
				items.add(item);
			} else if (file.getAttrs().isDir()) {
				if (".".equals(file.getFilename()) || "..".equals(file.getFilename())) {
					continue;
				}
				String path = dir + file.getFilename() + PATH_SEPARATOR;
				List<DiffItemImpl> itemsLocal = getItems(client, dirRoot, path, index, pattern);
				items.addAll(itemsLocal);
			}
		}

		Collections.sort(items, comparatorItem);

		return items;
	}

	public Collection<DiffEntity> loadCurrents() throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			LOG.debug("diff: " + code);
			List<DiffEntity> diffEntities = new ArrayList<DiffEntity>();

			if (dirInfos.isEmpty()) {
				LOG.trace("OK");
				return diffEntities;
			}
			Client client = connect();

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
				disconnect(client);
			}
			LOG.trace("OK");
			return diffEntities;
		} finally {
			LOG.trace("END");
		}
	}

	private DiffEntity loadCurrent(Client client, DirInfo dirInfo) throws BaseException {
		LOG.trace("BEGIN");
		try {
			long lastModified = getLastModifiedDirectory(client, dirInfo.getPath(), dirInfo.getPath(), 0,
					dirInfo.getPattern());
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

	private static long getLastModifiedDirectory(Client client, String dirRoot, String dir, long lastModified,
			Pattern pattern) throws BaseException {

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

	private static DiffEntity findGroup(Collection<DiffEntity> diffEntities, String name) {
		if (diffEntities != null) {
			for (DiffEntity diffEntity : diffEntities) {
				if (name.equals(diffEntity.getName())) {
					return diffEntity;
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

	private String adaptEntityName(String name) {
		if (UtilsString.isEmpty(code)) {
			return name;
		} else {
			return code + "." + name;
		}
	}
}
