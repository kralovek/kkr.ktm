package kkr.ktm.main;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.main.AbstractMain;
import kkr.common.main.Config;
import kkr.ktm.domains.orchestrator.components.batchdev.BatchDev;

public class MainDev extends AbstractMain {
	private static final Logger LOG = Logger.getLogger(MainDev.class);

	private static final DateFormat BATCH_ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");

	public static void main(String[] args) throws BaseException {
		System.out.println("KTM");
		MainDev main = new MainDev();
		main.run(args);
	}

	public void run(String[] args) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Config config = config(getClass(), Config.class, args);
			BatchDev batch = createBean(config, BatchDev.class, null, null);

			workRun(batch, config);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workRun(BatchDev batchDev, Config config) throws ConfigurationException, BaseException {
		LOG.trace("BEGIN");
		try {
			String batchId = generateBatchId();

			LOG.info("==============================");
			LOG.info("RUN [" + batchId + "]");
			LOG.info("------------------------------");

			File dirWorkingDirectory = getWorkingDirectory();
			LOG.info("WORKING DIRECTORY: " + dirWorkingDirectory.getAbsolutePath());

			printConfig(config);

			batchDev.run();

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void printConfig(Config config) {
		LOG.info("CONFIG: " + config.toString());
	}

	private String generateBatchId() {
		String batchId = BATCH_ID_DATE_FORMAT.format(new Date());
		return batchId;
	}

	private File getWorkingDirectory() {
		File dirCurrent = new File(System.getProperty("user.dir", "."));
		try {
			dirCurrent = dirCurrent.getCanonicalFile();
		} catch (IOException ex) {
			// nothing to do
		}
		return dirCurrent;
	}
}
