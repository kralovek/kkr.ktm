package kkr.ktm.domains.excel.components.structureloader.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import kkr.ktm.domains.excel.data.ExcelIdCell;
import kkr.ktm.domains.excel.data.Io;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.domains.excel.data.StructureParameterE;
import kkr.ktm.domains.excel.data.StructureParameterI;
import kkr.ktm.domains.excel.data.StructureParameterO;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureTest;

public class StructureSheetImpl implements StructureSheet {

	private String name;
	private Orientation orientation;

	Integer indexCode;

	Integer indexActive;

	Integer indexOrder;

	Integer indexGroup;

	Integer indexStatus;

	Integer indexDynStatus;

	Integer indexName;

	Integer indexDescription;
	private ExcelIdCell indexStatusSheetOk;
	private ExcelIdCell indexStatusSheetKo;
	private ExcelIdCell indexStatusSheetSkip;

	private Map<String, StructureTest> tests = new LinkedHashMap<String, StructureTest>();

	private Map<String, StructureParameterI> parametersI = new LinkedHashMap<String, StructureParameterI>();
	private Map<String, StructureParameterE> parametersE = new LinkedHashMap<String, StructureParameterE>();
	private Map<String, StructureParameterO> parametersO = new LinkedHashMap<String, StructureParameterO>();

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

	public Iterator<StructureParameterI> iteratorParametersI() {
		return parametersI.values().iterator();
	}

	public Iterator<StructureParameterO> iteratorParametersO() {
		return parametersO.values().iterator();
	}

	public Iterator<StructureParameterE> iteratorParametersE() {
		return parametersE.values().iterator();
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

	public StructureParameterI findParameterI(String name) {
		return parametersI.get(name);
	}

	public StructureParameterO findParameterO(String name) {
		return parametersO.get(name);
	}

	public StructureParameterE findParameterE(String name) {
		return parametersE.get(name);
	}

	public void addSystemParameter(SystemParameter systemParameter, int index) {
		switch (systemParameter) {
		case CODE:
			indexCode = index;
			break;
		case ACTIVE:
			indexActive = index;
			break;
		case NAME:
			indexName = index;
			break;
		case DESCRIPTION:
			indexDescription = index;
			break;
		case ORDER:
			indexOrder = index;
			break;
		case GROUP:
			indexGroup = index;
			break;
		case STATUS:
			indexStatus = index;
			break;
		case DYNSTATUS:
			indexDynStatus = index;
			break;

		default:
			break;
		}
	}

	public void addParameter(Io io, String name, int index) {
		switch (io) {
		case I:
			StructureParameterI structureParameterI = new StructureParameterI(name, index);
			parametersI.put(name, structureParameterI);
			break;
		case E:
			StructureParameterE structureParameterE = new StructureParameterE(name, index);
			parametersE.put(name, structureParameterE);
			break;
		case O:
			StructureParameterO structureParameterO = parametersO.get(name);
			if (structureParameterO == null) {
				structureParameterO = new StructureParameterO(name);
				parametersO.put(name, structureParameterO);
			}
			structureParameterO.addIndex(index);
			break;
		default:
			throw new IllegalArgumentException("Unsupported IO: " + io);
		}
	}

	public void addTest(StructureTest structureTest) {
		tests.put(structureTest.getCode(), structureTest);
	}

	public Integer getIndexCode() {
		return indexCode;
	}

	public Integer getIndexActive() {
		return indexActive;
	}

	public Integer getIndexOrder() {
		return indexOrder;
	}

	public Integer getIndexGroup() {
		return indexGroup;
	}

	public Integer getIndexStatus() {
		return indexStatus;
	}

	public Integer getIndexDynStatus() {
		return indexDynStatus;
	}

	public Integer getIndexName() {
		return indexName;
	}

	public Integer getIndexDescription() {
		return indexDescription;
	}
}
