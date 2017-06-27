package kkr.ktm.domains.excel.components.structureloader.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.domains.excel.data.ExcelIdCell;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.domains.excel.data.StructureParameter;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureTest;

public class StructureSheetImpl implements StructureSheet {

	private String name;
	private Orientation orientation;

	private int indexStatusTest;
	private ExcelIdCell indexStatusSheetOk;
	private ExcelIdCell indexStatusSheetKo;
	private ExcelIdCell indexStatusSheetSkip;

	private Map<String, StructureTest> tests = new LinkedHashMap<String, StructureTest>();

	private Map<String, StructureParameter> parametersI = new LinkedHashMap<String, StructureParameter>();
	private Map<String, StructureParameter> parametersO = new LinkedHashMap<String, StructureParameter>();
	private Map<String, StructureParameter> parametersE = new LinkedHashMap<String, StructureParameter>();

	public StructureSheetImpl(String name, Orientation orientation) {
		this.name = name;
		this.orientation = orientation;
	}

	public String getName() {
		return name;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Iterator<StructureTest> iteratorTests() {
		return tests.values().iterator();
	}

	public Iterator<StructureParameter> iteratorParametersI() {
		return parametersI.values().iterator();
	}

	public Iterator<StructureParameter> iteratorParametersO() {
		return parametersO.values().iterator();
	}

	public Iterator<StructureParameter> iteratorParametersE() {
		return parametersE.values().iterator();
	}

	public int getIndexStatusTest() {
		return indexStatusTest;
	}

	public void setIndexStatusTest(int indexStatusTest) {
		this.indexStatusTest = indexStatusTest;
	}

	public ExcelIdCell getIndexStatusSheetOk() {
		return indexStatusSheetOk;
	}

	public void setIndexStatusSheetOk(ExcelIdCell indexStatusSheetOk) {
		this.indexStatusSheetOk = indexStatusSheetOk;
	}

	public ExcelIdCell getIndexStatusSheetKo() {
		return indexStatusSheetKo;
	}

	public void setIndexStatusSheetKo(ExcelIdCell indexStatusSheetKo) {
		this.indexStatusSheetKo = indexStatusSheetKo;
	}

	public ExcelIdCell getIndexStatusSheetSkip() {
		return indexStatusSheetSkip;
	}

	public void setIndexStatusSheetSkip(ExcelIdCell indexStatusSheetSkip) {
		this.indexStatusSheetSkip = indexStatusSheetSkip;
	}

	public StructureTest findTest(String code) {
		return tests.get(code);
	}

	public StructureParameter findParameterI(String name) {
		return parametersI.get(name);
	}

	public StructureParameter findParameterO(String name) {
		return parametersO.get(name);
	}

	public StructureParameter findParameterE(String name) {
		return parametersE.get(name);
	}

	public void addParameter(StructureParameter structureParameter) {
		switch (structureParameter.getIo()) {
			case I :
				parametersI.put(structureParameter.getName(), structureParameter);
				break;
			case O :
				parametersO.put(structureParameter.getName(), structureParameter);
				break;
			case E :
				parametersE.put(structureParameter.getName(), structureParameter);
				break;

			default :
				throw new IllegalArgumentException("Unsupported IO: " + structureParameter.getIo());
		}
	}

	public void addTest(StructureTest structureTest) {
		tests.put(structureTest.getCode(), structureTest);
	}
}
