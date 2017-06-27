package kkr.ktm.batchs.convertormttktm;

import java.io.File;

import kkr.ktm.components.tablereader.TableReader;
import kkr.ktm.components.tablewriter.TableWriter;
import kkr.ktm.exception.ConfigurationException;

public abstract class BatchConvertorMttKtmFwk {
	private boolean configured;

	protected TableReader tableReader;
	protected TableWriter tableWriter;
	protected File dir;

	public void config() throws ConfigurationException {
		configured = false;
		if (tableReader == null) {
			throw new ConfigurationException("Parameter 'tableReader' is not configured");
		}
		if (tableWriter == null) {
			throw new ConfigurationException("Parameter 'tableWriter' is not configured");
		}
		if (dir == null) {
			throw new ConfigurationException("Parameter 'dir' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public TableReader getTableReader() {
		return tableReader;
	}

	public void setTableReader(TableReader tableReader) {
		this.tableReader = tableReader;
	}

	public TableWriter getTableWriter() {
		return tableWriter;
	}

	public void setTableWriter(TableWriter tableWriter) {
		this.tableWriter = tableWriter;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}
}
