package kkr.ktm.domains.excel.components.structureloader;

import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureWorkbook;
import kkr.ktm.exception.BaseException;
import kkr.ktm.utils.excel.ExcelPosition;

public interface StructureLoader {

	StructureSheet loadStructureSheet(ExcelPosition excelPosition, TSheet tSheet) throws BaseException;

	StructureWorkbook loadStructureWorkbook(ExcelPosition excelPosition, TWorkbook tWorkbook) throws BaseException;
}
