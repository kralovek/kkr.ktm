package kkr.ktm.domains.excel.components.valuegenerator;

import kkr.ktm.domains.tests.data.ValuePattern;
import kkr.common.errors.BaseException;
import kkr.common.utils.excel.ExcelPosition;

public interface ValueGenerator {

	boolean compareValues(ExcelPosition excelPositionE, ValuePattern valuePatternE, Object valueO) throws BaseException;

	ValuePattern parsePattern(ExcelPosition excelPosition, Object value) throws BaseException;

	Object parseValue(ExcelPosition excelPosition, Object value) throws BaseException;

	Object formatValue(ExcelPosition excelPosition, Object value) throws BaseException;
}
