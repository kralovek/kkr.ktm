package kkr.ktm.components.cleanmanager.filesystem.ftp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import kkr.ktm.components.cleanmanager.CleanManager;
import kkr.ktm.components.cleanmanager.filesystem.DirInfo;
import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.errors.TechnicalException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class CleanManagerSFtpJsch extends CleanManagerFtpFwk implements
		CleanManager {
	private static final Logger LOG = Logger
			.getLogger(CleanManagerSFtpJsch.class);

	private static interface FileFilter {
		boolean accept(LsEntry file);
	}

	private static class FileFilterBase {
		private String parentPath;
		private List<Pattern> patterns;

		public FileFilterBase(String parentPath, List<Pattern> patterns) {
			this.patterns = patterns;
			this.parentPath = adaptDirPath(parentPath);
		}

		private String adaptDirPath(String path) {
			if (path == null) {
				return "";
			}
			if (!path.isEmpty() && !path.endsWith("/")) {
				return path + "/";
			} else {
				return path;
			}
		}

		protected boolean matches(LsEntry file) {
			if (patterns == null) {
				return false;
			}
			String fileRelativePath = parentPath + file.getFilename();
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
		public FileFilterMatchingFile(String parentPath, List<Pattern> patterns) {
			super(parentPath, patterns);
		}

		public boolean accept(LsEntry file) {
			file.getAttrs().isDir();
			return !file.getAttrs().isDir() && matches(file);
		}
	}

	private static class FileFilterMatchingDir extends FileFilterBase implements
			FileFilter {
		public FileFilterMatchingDir(String parentPath, List<Pattern> patterns) {
			super(parentPath, patterns);
		}

		public boolean accept(LsEntry file) {
			return file.getAttrs().isDir() && matches(file);
		}
	}

	private static class FileFilterNonMatchingDir extends FileFilterBase
			implements FileFilter {
		public FileFilterNonMatchingDir(String parentPath,
				List<Pattern> patterns) {
			super(parentPath, patterns);
		}

		public boolean accept(LsEntry file) {
			return file.getAttrs().isDir() && !matches(file);
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
				
				Set<String> nonRemoved = removeFiles(dirInfo.getPath(), group.getPatterns());
				if (!nonRemoved.isEmpty()) {
					totalNonRemoved += toStringNonRemoved(dirInfo.getPath(), nonRemoved) + "\n";
				}
			}

			if (!totalNonRemoved.isEmpty()) {
				throw new TechnicalException(
						"Cannot remove some files: " + totalNonRemoved);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private String toStringNonRemoved(String rootPath, Set<String> nonRemoved) {
		StringBuffer buffer = new StringBuffer();
		for (String file : nonRemoved) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}
			buffer.append(file);
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

	private Set<String> removeFiles(String dir, List<String> strPatterns)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			List<Pattern> patterns = new ArrayList<Pattern>();
			if (strPatterns != null) {
				for (String strPattern : strPatterns) {
					Pattern pattern = Pattern.compile(strPattern);
					patterns.add(pattern);
				}
			}

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

				changeDirectory(sftpChannel, dir);

				Set<String> nonRemoved = workDirRemoveFiles(sftpChannel, "",
						patterns);

				LOG.trace("OK");
				return nonRemoved;
			} catch (JSchException ex) {
				throw new TechnicalException(
						"Impossible to remove files from the remote directory: "
								+ dir, ex);
			} finally {
				if (sftpChannel != null) {
					sftpChannel.disconnect();
				}
				if (session != null) {
					session.disconnect();
				}
			}

		} finally {
			LOG.trace("END");
		}
	}

	Set<String> workDirRemoveFiles(ChannelSftp channel, String pathDir,
			List<Pattern> patterns) throws BaseException {
		Set<String> nonRemoved = new LinkedHashSet<String>();

		Vector<LsEntry> files;
		try {
			files = channel.ls(pathDir.isEmpty() ? "." : pathDir);
		} catch (SftpException ex) {
			throw new TechnicalException(
					"Cannot read the content of the directory: " + pathDir, ex);
		}

		FileFilter fileFilterMatchingFile = new FileFilterMatchingFile(pathDir,
				patterns);
		FileFilter fileFilterMatchingDir = new FileFilterMatchingDir(pathDir,
				patterns);
		FileFilter fileFilterNonMatchingDir = new FileFilterNonMatchingDir(
				pathDir, patterns);

		for (LsEntry lsEntry : files) {
			if ("..".equals(lsEntry.getFilename())) {
				continue;
			}
			if (".".equals(lsEntry.getFilename())) {
				continue;
			}
			if (fileFilterMatchingFile.accept(lsEntry)) {
				try {
					channel.rm((pathDir.isEmpty() ? "" : pathDir + "/")
							+ lsEntry.getFilename());
				} catch (SftpException ex) {
					nonRemoved.add((pathDir.isEmpty() ? "" : pathDir + "/")
							+ lsEntry.getFilename());
				}
				continue;
			}
			if (fileFilterMatchingDir.accept(lsEntry)) {
				Set<String> notRemovedLoc = removeNonemptyDir(
						channel,
						(pathDir.isEmpty() ? "" : pathDir + "/")
								+ lsEntry.getFilename());
				nonRemoved.addAll(notRemovedLoc);
				continue;
			}
			if (fileFilterNonMatchingDir.accept(lsEntry)) {
				Set<String> nonRemovedLoc = workDirRemoveFiles(
						channel,
						(pathDir.isEmpty() ? "" : pathDir + "/")
								+ lsEntry.getFilename(), patterns);
				nonRemoved.addAll(nonRemovedLoc);
				continue;
			}
		}

		return nonRemoved;
	}

	private Set<String> removeNonemptyDir(ChannelSftp channel, String pathDir)
			throws BaseException {
		Set<String> nonRemoved = new LinkedHashSet<String>();

		Vector<LsEntry> files;
		try {
			files = channel.ls(pathDir);
		} catch (SftpException ex) {
			throw new TechnicalException(
					"Cannot read the content of the directory: " + pathDir, ex);
		}

		for (LsEntry file : files) {
			if ("..".equals(file.getFilename())) {
				continue;
			}
			if (".".equals(file.getFilename())) {
				continue;
			}
			if (!file.getAttrs().isDir()) {
				try {
					channel.rm(pathDir + "/" + file.getFilename());
				} catch (SftpException ex) {
					nonRemoved.add(pathDir + "/" + file.getFilename());
				}
			} else if (file.getAttrs().isDir()) {
				Set<String> nonRemovedLoc = removeNonemptyDir(channel, pathDir
						+ "/" + file.getFilename());
				nonRemoved.addAll(nonRemovedLoc);
			}
		}
		try {
			channel.rmdir(pathDir);
		} catch (SftpException ex) {
			nonRemoved.add(pathDir);
		}
		return nonRemoved;
	}

	private void changeDirectory(ChannelSftp client, String dirRemote)
			throws BaseException {
		try {
			client.cd(dirRemote);
		} catch (SftpException ex) {
			throw new TechnicalException(
					"Imposible to change the remote directory to: " + dirRemote,
					ex);
		}
	}

	public static final void main(String[] argv) throws Exception {
		CleanManagerSFtpJsch fileManagerLocal = new CleanManagerSFtpJsch();
		fileManagerLocal.setFtpHost("10.68.252.74");
		fileManagerLocal.setFtpPort(22);
		;
		fileManagerLocal.setFtpLogin("requea");
		fileManagerLocal.setFtpPassword("requea");
		fileManagerLocal.config();

		List<String> patterns = new ArrayList<String>();

		patterns.add("config");
		patterns.add("trace/COWD_CONFIG/INT");
		patterns.add("trace/COWD_CONFIG/LOC/.*\\.gz");
		patterns.add("run.*COWD_CONFIG.*");

		fileManagerLocal.removeFiles("/home/requea/kkr/root", patterns);
	}
}
