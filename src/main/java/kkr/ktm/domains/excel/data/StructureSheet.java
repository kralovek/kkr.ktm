package kkr.ktm.domains.excel.data;

import java.util.Iterator;

import kkr.ktm.domains.excel.components.structureloader.base.SystemParameter;

public interface StructureSheet {

	String getName();

	Orientation getOrientation();

	Iterator<StructureTest> iteratorTests();

	Iterator<StructureParameterI> iteratorParametersI();

	Iterator<StructureParameterO> iteratorParametersO();

	Iterator<StructureParameterE> iteratorParametersE();

	StructureTest findTest(String code);

	StructureParameterI findParameterI(String name);

	StructureParameterO findParameterO(String name);

	StructureParameterE findParameterE(String name);

	ExcelIdCell getIndexStatusSheetOk();

	ExcelIdCell getIndexStatusSheetKo();

	ExcelIdCell getIndexStatusSheetSkip();

	void addParameter(Io io, String name, int index);

	void addSystemParameter(SystemParameter systemParameter, int index);

	void addTest(StructureTest structureTest);

	Integer getIndexCode();

	Integer getIndexActive();

	Integer getIndexOrder();

	Integer getIndexGroup();

	Integer getIndexStatus();

	Integer getIndexDynStatus();

	Integer getIndexName();

	Integer getIndexDescription();
}
