package kkr.ktm.components.cleanmanager.filesystem.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import kkr.ktm.components.cleanmanager.CleanManager;
import kkr.ktm.components.cleanmanager.filesystem.DirInfo;
import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.errors.TechnicalException;
import kkr.ktm.utils.ftp.UtilsFtp;

public class CleanManagerFtp extends CleanManagerFtpFwk implements CleanManager {
	private static final Logger LOG = Logger
			.getLogger(CleanManagerFtp.class);

	private static interface FileFilter {
		boolean accept(FTPFile file);
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

		protected boolean matches(FTPFile file) {
			if (patterns == null) {
				return false;
			}
			String fileRelativePath = parentPath + file.getName();
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

		public boolean accept(FTPFile file) {
			return file.isFile() && matches(file);
		}
	}

	private static class FileFilterMatchingDir extends FileFilterBase implements
			FileFilter {
		public FileFilterMatchingDir(String parentPath, List<Pattern> patterns) {
			super(parentPath, patterns);
		}

		public boolean accept(FTPFile file) {
			return file.isDirectory() && matches(file);
		}
	}

	private static class FileFilterNonMatchingDir extends FileFilterBase
			implements FileFilter {
		public FileFilterNonMatchingDir(String parentPath,
				List<Pattern> patterns) {
			super(parentPath, patterns);
		}

		public boolean accept(FTPFile file) {
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
				
				Set<FTPFile> nonRemoved = removeFiles(dirInfo.getPath(), group.getPatterns());
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

	private String toStringNonRemoved(String rootPath, Set<FTPFile> nonRemoved) {
		StringBuffer buffer = new StringBuffer();
		for (FTPFile file : nonRemoved) {
			if (buffer.length() != 0) {
				buffer.append(",");
			}
			buffer.append(file.getName());
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

	public Set<FTPFile> removeFiles(String dir, List<String> strPatterns)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<Pattern> patterns = new ArrayList<Pattern>();
			for (String strPattern : strPatterns) {
				Pattern pattern = Pattern.compile(strPattern);
				patterns.add(pattern);
			}

			FTPClient client = UtilsFtp.connect(ftpHost, ftpPort, ftpLogin,
					ftpPassword);

			try {
				changeDirectory(client, dir);

				Set<FTPFile> nonRemoved = workDirRemoveFiles(client, "",
						patterns);
				
				if (!nonRemoved.isEmpty()) {
					StringBuffer buffer = new StringBuffer();
					for (FTPFile file : nonRemoved) {
						if (buffer.length() != 0) {
							buffer.append(",");
						}
						buffer.append(file.getName());
					}
					throw new TechnicalException(
							"Cannot remove some files from the directory "
									+ dir + ": " + buffer.toString());
				}

				LOG.trace("OK");
				return nonRemoved;
			} finally {
				try {
					if (client != null) {
						client.disconnect();
					}
				} catch (IOException ex) {
					// rien � faire
				}
			}

		} finally {
			LOG.trace("END");
		}
	}

	private Set<FTPFile> workDirRemoveFiles(FTPClient client, String pathDir,
			List<Pattern> patterns) throws BaseException {
		try {
			Set<FTPFile> nonRemoved = new LinkedHashSet<FTPFile>();

			String pwd = client.printWorkingDirectory();

			FileFilter fileFilterMatchingFile = new FileFilterMatchingFile(
					pathDir, patterns);
			FileFilter fileFilterMatchingDir = new FileFilterMatchingDir(
					pathDir, patterns);
			FileFilter fileFilterNonMatchingDir = new FileFilterNonMatchingDir(
					pathDir, patterns);

			FTPFile[] ftpFiles = client.listFiles();

			for (FTPFile ftpFile : ftpFiles) {
				if (fileFilterMatchingFile.accept(ftpFile)) {
					if (!client.deleteFile((pathDir.isEmpty() ? "" : pathDir + "/") + ftpFile)) {
						nonRemoved.add(ftpFile);
					}
					continue;
				}
				if (fileFilterMatchingDir.accept(ftpFile)) {
					if (!client.deleteFile((pathDir.isEmpty() ? "" : pathDir + "/") + ftpFile)) {
						nonRemoved.add(ftpFile);
					}
					continue;
				}
				if (fileFilterNonMatchingDir.accept(ftpFile)) {
					changeDirectory(client, ftpFile.getName());
					Set<FTPFile> nonRemovedLoc = workDirRemoveFiles(client,
							(pathDir.isEmpty() ? "" : pathDir + "/") + ftpFile.getName(), patterns);
					nonRemoved.addAll(nonRemovedLoc);
					changeDirectory(client, pwd);
					continue;
				}
			}

			changeDirectory(client, pwd);

			return nonRemoved;
		} catch (IOException ex) {
			throw new TechnicalException(
					"Impossible to remove files from the remote directory: "
							+ pathDir, ex);
		}
	}

	private void changeDirectory(FTPClient client, String dirRemote)
			throws BaseException {
		try {
			if (!client.changeWorkingDirectory(dirRemote)) {
				throw new TechnicalException(
						"The remote directory does not exist: " + dirRemote);
			}
		} catch (IOException ex) {
			throw new TechnicalException(
					"Imposible to change the remote directory to: " + dirRemote,
					ex);
		}
	}
}
