package kkr.ktm.main;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import kkr.ktm.domains.orchestrator.components.batchktm.BatchKtm;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TestError;
import kkr.ktm.utils.UtilsBean;
import kkr.ktm.utils.errors.TreatErrors;

public class MainKtm {
	private static final Logger LOG = Logger.getLogger(MainKtm.class);

	private static final String ID_BEAN = "batchKtm";
	private static final DateFormat BATCH_ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");

	public static void main(final String[] pArgs) {
		MainKtm main = new MainKtm();
		main.work(pArgs);
	}

	private void work(String[] pArgs) {
		LOG.trace("BEGIN");
		try {
			ConfigKtm config = new ConfigKtm(pArgs);

			try {
				BeanFactory beanFactory = UtilsBean.createBeanFactory(config.getConfig(), null, null);

				BatchKtm batchKtm = beanFactory.getBean(ID_BEAN, BatchKtm.class);

				workRun(batchKtm, config);

			} catch (BeansException ex) {
				throw new ConfigurationException(ex.getMessage());
			}

			LOG.trace("OK");
		} catch (TestError testError) {
			throw testError;
		} catch (Exception ex) {
			TreatErrors.treatException(ex);
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

			printMessages(config);
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

	private void printMessages(ConfigKtm config) {
		if (!config.getMessages().isEmpty()) {
			LOG.info("MESSAGES: ");
			for (String message : config.getMessages()) {
				LOG.info("\t" + message);
			}
		}
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
