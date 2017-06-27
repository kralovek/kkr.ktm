package kkr.ktm.domains.excel.data;

import java.util.Map;

public interface StructureWorkbook {

	String getSource();

	Map<String, StructureSheet> getSheets();
}
