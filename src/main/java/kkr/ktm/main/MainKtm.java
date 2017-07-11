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
import kkr.ktm.domains.orchestrator.components.batchktm.BatchKtm;

public class MainKtm extends AbstractMain {
	private static final Logger LOG = Logger.getLogger(MainKtm.class);

	private static final DateFormat BATCH_ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");

	public static void main(String[] args) throws BaseException {
		MainKtm main = new MainKtm();
		main.run(args);
	}

	public void run(String[] args) throws BaseException {
		LOG.trace("BEGIN");
		try {
			ConfigKtm config = config(getClass(), ConfigKtm.class, args);
			BatchKtm batch = createBean(config, BatchKtm.class, null, null);

			workRun(batch, config);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workRun(BatchKtm batchKtm, ConfigKtm config) throws ConfigurationException, BaseException {
		LOG.trace("BEGIN");
		try {
			String batchId = generateBatchId();

			LOG.info("==============================");
			LOG.info("RUN [" + batchId + "]");
			LOG.info("------------------------------");

			File dirWorkingDirectory = getWorkingDirectory();
			LOG.info("WORKING DIRECTORY: " + dirWorkingDirectory.getAbsolutePath());

			printConfig(config);

			batchKtm.run(batchId, config.getSource());

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void printConfig(ConfigKtm config) {
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
