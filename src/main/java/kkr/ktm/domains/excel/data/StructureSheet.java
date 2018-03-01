package kkr.ktm.domains.excel.data;

import java.util.Iterator;

import kkr.ktm.domains.excel.components.structureloader.base.SystemParameter;

public interface StructureSheet {

	String getName();

	Orientation getOrientation();

	Iterator<StructureTest> iteratorTests();

	Iterator<StructureParameter> iteratorParametersI();

	Iterator<StructureParameter> iteratorParametersO();

	Iterator<StructureParameter> iteratorParametersE();

	StructureTest findTest(String code);

	StructureParameter findParameterI(String name);

	StructureParameter findParameterO(String name);

	StructureParameter findParameterE(String name);

	ExcelIdCell getIndexStatusSheetOk();

	ExcelIdCell getIndexStatusSheetKo();

	ExcelIdCell getIndexStatusSheetSkip();

	void addParameter(StructureParameter structureParameter);

	void addSystemParameter(SystemParameter systemParameter, int index);

	void addTest(StructureTest structureTest);

	Integer getIndexCode();

	Integer getIndexActive();

	Integer getIndexOrder();

	Integer getIndexGroup();

	Integer getIndexStatus();

	Integer getIndexName();

	Integer getIndexDescription();
}
