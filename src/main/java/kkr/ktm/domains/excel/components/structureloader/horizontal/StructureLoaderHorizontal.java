package kkr.ktm.domains.excel.components.structureloader.horizontal;

import kkr.common.utils.excel.ExcelPosition;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.data.Orientation;

public class StructureLoaderHorizontal extends StructureLoaderHorizontalFwk implements StructureLoader {

	protected Orientation orientation() {
		return Orientation.H;
	}

	protected void updateExcelPosition(ExcelPosition excelPosition, int indexCurrentParameter, int indexCurrentTest) {
		excelPosition.setRow(indexCurrentTest);
		excelPosition.setColumn(indexCurrentParameter);
	}

	protected TCell loadCell(TSheet tSheet, int indexCurrentParameter, int indexCurrentTest) {
		return excelAdapter.getCell(tSheet, indexCurrentTest, indexCurrentParameter);
	}

	protected boolean isLimitTest(TSheet tSheet, int indexCurrentTest) {
		return indexCurrentTest <= tSheet.getRowMax();
	}

	protected boolean isLimitParameter(TSheet tSheet, int indexCurrentParameter) {
		return indexCurrentParameter <= tSheet.getColumnMax();
	}
}
