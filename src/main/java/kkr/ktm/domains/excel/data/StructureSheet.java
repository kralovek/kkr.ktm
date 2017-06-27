package kkr.ktm.domains.excel.data;

import java.util.Iterator;

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

	int getIndexStatusTest();

	ExcelIdCell getIndexStatusSheetOk();

	ExcelIdCell getIndexStatusSheetKo();

	ExcelIdCell getIndexStatusSheetSkip();

	void addParameter(StructureParameter structureParameter);

	void addTest(StructureTest structureTest);
}
