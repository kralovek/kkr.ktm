package kkr.ktm.domains.excel.components.structureloader.base;

import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureWorkbook;

public class StructureWorkbookImpl implements StructureWorkbook {

	private String source;

	private Map<String, StructureSheet> sheets = new LinkedHashMap<String, StructureSheet>();

	public StructureWorkbookImpl(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public Map<String, StructureSheet> getSheets() {
		return sheets;
	}
}
