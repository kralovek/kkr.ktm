package kkr.ktm.utils.errors;

import org.apache.log4j.Logger;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.FunctionalException;
import kkr.ktm.exception.TechnicalException;

public final class TreatErrors {
	private static final Logger logger = Logger.getLogger(TreatErrors.class);

	private TreatErrors() {
		super();
	}

	private static final BaseException findCauseException(final Throwable pException) {
		for (Throwable throwable = pException; throwable != null; throwable = throwable.getCause()) {
			if (throwable instanceof BaseException) {
				return (BaseException) throwable;
			}
		}
		return null;
	}

	public static final void treatException(final Throwable throwable) {
		final BaseException baseException = findCauseException(throwable);
		if (baseException != null) {
			if (baseException instanceof ConfigurationException) {
				logger.error("###############################");
				logger.error("CONFIGURATION PROBLEM");
				logger.error("");
				logger.error("", baseException);
				printCauses(throwable);
				logger.error("###############################");
				return;
			} else if (baseException instanceof FunctionalException) {
				logger.error("###############################");
				logger.error("FUNCTIONAL PROBLEM");
				logger.error("");
				logger.error("", baseException);
				printCauses(throwable);
				logger.error("###############################");
				return;
			} else if (baseException instanceof TechnicalException) {
				logger.error("###############################");
				logger.error("TECHNICAL PROBLEM");
				logger.error("");
				printCauses(throwable);
				logger.error("");
				logger.error("", baseException);
				logger.error("###############################");
				return;
			}
		}

		logger.error("###############################");
		logger.error("UNEXPECTED PROBLEM");
		logger.error("");
		printCauses(throwable);
		logger.error("");
		logger.error("", throwable);
		logger.error("###############################");
	}

	private static void printCauses(Throwable throwable) {
		if (throwable == null) {
			return;
		}
		logger.error("REASON: ");
		logger.error(throwable.getMessage());
		Throwable th = throwable.getCause();
		for (int n = 1; th != null; th = th.getCause(), n++) {
			logger.error("CAUSE " + n + ": ");
			logger.error(th.getMessage());
		}
	}
}
