package kkr.ktm.components.lancer.infotel_table;

import java.util.Collection;

import kkr.ktm.exception.ConfigurationException;

public abstract class LancerInfotelTableFwk {
	private boolean configured;

	protected String keyData;
	protected Collection<String> keys;
	protected String sysParamPrefix;

	public void config() throws ConfigurationException {
		configured = false;
		if (keys == null || keys.isEmpty()) {
			throw new ConfigurationException("Parameter 'keys' is not configured or is empty");
		}
		if (sysParamPrefix == null || sysParamPrefix.isEmpty()) {
			sysParamPrefix = "";
		} else {
			sysParamPrefix += "/";
		}
		if (keyData == null) {
			throw new ConfigurationException("Parameter 'keyData' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getKeyData() {
		return keyData;
	}

	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}

	public Collection<String> getKeys() {
		return keys;
	}

	public void setKeys(Collection<String> keys) {
		this.keys = keys;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}
}
