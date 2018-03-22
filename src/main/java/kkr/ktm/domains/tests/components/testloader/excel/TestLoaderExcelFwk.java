package kkr.ktm.domains.tests.components.testloader.excel;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.tests.components.valueparser.ValueParser;

public abstract class TestLoaderExcelFwk {
	private boolean configured;

	protected ExcelAdapter excelAdapter;
	protected StructureLoader structureLoader;
	protected ValueParser valueParser;

	public void config() throws ConfigurationException {
		configured = false;
		if (structureLoader == null) {
			throw new ConfigurationException("Parameter 'structureLoader' is not configured");
		}
		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}
		if (valueParser == null) {
			throw new ConfigurationException("Parameter 'valueParser' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public StructureLoader getStructureLoader() {
		return structureLoader;
	}

	public void setStructureLoader(StructureLoader structureLoader) {
		this.structureLoader = structureLoader;
	}

	public ExcelAdapter getExcelAdapter() {
		return excelAdapter;
	}

	public void setExcelAdapter(ExcelAdapter excelAdapter) {
		this.excelAdapter = excelAdapter;
	}

	public ValueParser getValueParser() {
		return valueParser;
	}

	public void setValueParser(ValueParser valueParser) {
		this.valueParser = valueParser;
	}
}
