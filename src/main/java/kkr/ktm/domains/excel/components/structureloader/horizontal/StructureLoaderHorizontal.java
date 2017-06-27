package kkr.ktm.domains.excel.components.structureloader.horizontal;

import org.apache.log4j.Logger;

import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.utils.excel.ExcelPosition;

public class StructureLoaderHorizontal extends StructureLoaderHorizontalFwk implements StructureLoader {
	private static final Logger LOG = Logger.getLogger(StructureLoaderHorizontal.class);

	protected Orientation orientation() {
		return Orientation.H;
	}

	protected TCell loadCellIo(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexIo);
		TCell tCellIo = excelAdapter.getCell(tSheet, indexIo, index);
		return tCellIo;
	}

	protected TCell loadCellParameter(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexParameter);
		TCell tCellParameter = excelAdapter.getCell(tSheet, indexParameter, index);
		return tCellParameter;
	}

	protected TCell loadCellActive(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexActive);
		TCell tCellActive = excelAdapter.getCell(tSheet, index, indexActive);
		return tCellActive;
	}

	protected TCell loadCellCode(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexCode);
		TCell tCellCode = excelAdapter.getCell(tSheet, index, indexCode);
		return tCellCode;
	}

	protected TCell loadCellGroup(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexGroup);
		TCell tCellGroup = excelAdapter.getCell(tSheet, index, indexGroup);
		return tCellGroup;
	}

	protected TCell loadCellOrder(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexOrder);
		TCell tCellOrder = excelAdapter.getCell(tSheet, index, indexOrder);
		return tCellOrder;
	}

	protected TCell loadCellName(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexName);
		TCell tCellName = excelAdapter.getCell(tSheet, index, indexName);
		return tCellName;
	}

	protected TCell loadCellDescription(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexDescription);
		TCell tCellDescription = excelAdapter.getCell(tSheet, index, indexDescription);
		return tCellDescription;
	}

	protected TCell loadCellStatus(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexStatusTest);
		TCell tCellStatus = excelAdapter.getCell(tSheet, index, indexStatusTest);
		return tCellStatus;
	}

	protected boolean isIgnoredTest(int index) {
		return rowsIgnored.contains(index) //
				|| index == indexParameter //
				|| index == indexIo;
	}

	protected boolean isIgnoredParameter(int index) {
		return columnsIgnored.contains(index) //
				|| index == indexCode //
				|| index == indexActive //
				|| indexGroup != null && index == indexGroup //
				|| indexOrder != null && index == indexOrder //
				|| indexName != null && index == indexName //
				|| indexDescription != null && index == indexDescription;
	}

	protected boolean isLimitTest(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(index);
		return index <= tSheet.getRowMax();
	}

	protected boolean isLimitParameter(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(index);
		return index <= tSheet.getColumnMax();
	}
}
