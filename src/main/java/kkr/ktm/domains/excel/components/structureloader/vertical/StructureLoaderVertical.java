package kkr.ktm.domains.excel.components.structureloader.vertical;

import kkr.common.utils.excel.ExcelPosition;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.data.Orientation;

public class StructureLoaderVertical extends StructureLoaderVerticalFwk implements StructureLoader {

	protected Orientation orientation() {
		return Orientation.V;
	}

	protected void updateExcelPosition(ExcelPosition excelPosition, int indexCurrentParameter, int indexCurrentTest) {
		excelPosition.setRow(indexCurrentParameter);
		excelPosition.setColumn(indexCurrentTest);
	}

	protected TCell loadCell(TSheet tSheet, int indexCurrentParameter, int indexCurrentTest) {
		TCell tCell = excelAdapter.getCell(tSheet, indexCurrentParameter, indexCurrentTest);
		return tCell;
	}

	protected boolean isLimitTest(TSheet tSheet, int indexCurrentTest) {
		return indexCurrentTest <= tSheet.getColumnMax();
	}

	protected boolean isLimitParameter(TSheet tSheet, int indexCurrentParameter) {
		return indexCurrentParameter <= tSheet.getRowMax();
	}
}
