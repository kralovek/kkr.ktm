package kkr.ktm.domains.debug.main;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import kkr.ktm.domains.debug.components.testsimple.BatchTestSimple;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.TestError;
import kkr.ktm.utils.UtilsBean;
import kkr.ktm.utils.errors.TreatErrors;

public class MainTestSimple {
	private static final Logger LOG = Logger.getLogger(MainTestSimple.class);

	private static final String ID_BEAN = "batchTestSimple";
	private static final String CONFIG = "spring-main-testsimple.xml";

	public static void main(final String[] pArgs) {
		MainTestSimple main = new MainTestSimple();
		main.work(pArgs);
	}

	private void work(String[] pArgs) {
		LOG.trace("BEGIN");
		try {
			try {
				BeanFactory beanFactory = UtilsBean.createBeanFactory(CONFIG, null, null);

				BatchTestSimple batchKtmSimple = beanFactory.getBean(ID_BEAN, BatchTestSimple.class);

				batchKtmSimple.run();

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
}
