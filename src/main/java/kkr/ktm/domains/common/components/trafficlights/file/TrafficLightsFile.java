package kkr.ktm.domains.common.components.trafficlights.file;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.trafficlights.TrafficLights;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.errors.StopException;

public class TrafficLightsFile extends TrafficLightsFileFwk implements TrafficLights {
	private static final Logger LOG = Logger.getLogger(TrafficLightsFile.class);

	public boolean isRun() throws BaseException {
		if (file.exists()) {
			LOG.info("STOP: " + file.getAbsolutePath());
			if (!file.delete()) {
				throw new TechnicalException("Cannot remove stop file: " + file.getAbsolutePath());
			}
			return false;
		}
		return true;
	}

	public void checkRun() throws BaseException, StopException {
		if (isRun()) {
			throw new StopException();
		}
	}
}
