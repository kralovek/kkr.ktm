package kkr.ktm.domains.excel.components.exceladapter.poi;

import java.text.SimpleDateFormat;
import java.util.Date;

import kkr.common.errors.ConfigurationException;

public class ExcelAdapterPoiFwk {
	private boolean configured;

	private String _formatDate;
	protected SimpleDateFormat formatDate;

	private String _formatTime;
	protected SimpleDateFormat formatTime;

	public void config() throws ConfigurationException {
		configured = false;
		if (_formatDate == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter 'formatDate' is not configured");
		} else {
			try {
				formatDate = new SimpleDateFormat(_formatDate);
				formatDate.format(new Date());
			} catch (Exception ex) {
				throw new ConfigurationException(
						getClass().getSimpleName() + ": Parameter 'formatDate' has bad value: " + _formatDate);
			}
		}
		if (_formatTime == null) {
			_formatTime = _formatDate;
			formatTime = formatDate;
		} else {
			try {
				formatTime = new SimpleDateFormat(_formatTime);
				formatTime.format(new Date());
			} catch (Exception ex) {
				throw new ConfigurationException(
						getClass().getSimpleName() + ": Parameter 'formatTime' has bad value: " + _formatTime);
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": the component must be configured.");
		}
	}

	public String getFormatDate() {
		return _formatDate;
	}

	public void setFormatDate(String formatDate) {
		this._formatDate = formatDate;
	}

	public String getFormatTime() {
		return _formatTime;
	}

	public void setFormatTime(String formatTime) {
		this._formatTime = formatTime;
	}
}
