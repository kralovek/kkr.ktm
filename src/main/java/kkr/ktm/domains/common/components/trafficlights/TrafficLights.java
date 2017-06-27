package kkr.ktm.domains.common.components.trafficlights;

import kkr.ktm.exception.BaseException;
import kkr.ktm.utils.errors.StopException;

public interface TrafficLights {

	boolean isRun() throws BaseException;

	void checkRun() throws BaseException, StopException;
}
