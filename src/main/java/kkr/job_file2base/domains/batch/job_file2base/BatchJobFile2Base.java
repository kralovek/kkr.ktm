package kkr.job_file2base.domains.batch.job_file2base;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.FunctionalException;
import kkr.ktm.exception.TechnicalException;

public class BatchJobFile2Base extends BatchJobFile2BaseFwk {
	private static final Logger LOG = Logger.getLogger(BatchJobFile2Base.class);

	private static final FileFilter FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return file.getName().toLowerCase().endsWith(".csv");
		}
	};

	public void run() throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<File> files = loadFiles();

			for (File file : files) {
				workFile(file);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workFile(File file) {
		LOG.trace("BEGIN: " + file.getAbsolutePath());
		try {
			LOG.trace("OK");
		} finally {
			LOG.trace("END: " + file.getAbsolutePath());
		}
	}

	private Collection<File> loadFiles() throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (dir.isDirectory()) {
				throw new FunctionalException("Directory does not exist: " + dir.getAbsolutePath());
			}

			File[] files = dir.listFiles(FILE_FILTER);

			if (files == null) {
				throw new TechnicalException("Cannot access the directory: " + dir.getAbsolutePath());
			}

			Collection<File> retval = new ArrayList<File>();

			for (File file : files) {
				retval.add(file);
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
