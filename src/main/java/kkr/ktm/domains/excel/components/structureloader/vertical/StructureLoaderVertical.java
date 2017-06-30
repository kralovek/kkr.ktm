package kkr.ktm.domains.excel.components.structureloader.vertical;

import org.apache.log4j.Logger;

import kkr.common.utils.excel.ExcelPosition;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.data.Orientation;

public class StructureLoaderVertical extends StructureLoaderVerticalFwk implements StructureLoader {
	private static final Logger LOG = Logger.getLogger(StructureLoaderVertical.class);

	protected Orientation orientation() {
		return Orientation.V;
	}

	protected TCell loadCellIo(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexIo);
		TCell tCellIo = excelAdapter.getCell(tSheet, index, indexIo);
		return tCellIo;
	}

	protected TCell loadCellParameter(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(indexParameter);
		TCell tCellParameter = excelAdapter.getCell(tSheet, index, indexParameter);
		return tCellParameter;
	}

	protected TCell loadCellActive(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexActive);
		TCell tCellActive = excelAdapter.getCell(tSheet, indexActive, index);
		return tCellActive;
	}

	protected TCell loadCellCode(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexCode);
		TCell tCellCode = excelAdapter.getCell(tSheet, indexCode, index);
		return tCellCode;
	}

	protected TCell loadCellGroup(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexGroup);
		TCell tCellGroup = excelAdapter.getCell(tSheet, indexGroup, index);
		return tCellGroup;
	}

	protected TCell loadCellOrder(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexOrder);
		TCell tCellOrder = excelAdapter.getCell(tSheet, indexOrder, index);
		return tCellOrder;
	}

	protected TCell loadCellName(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexName);
		TCell tCellName = excelAdapter.getCell(tSheet, indexName, index);
		return tCellName;
	}

	protected TCell loadCellDescription(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexDescription);
		TCell tCellDescription = excelAdapter.getCell(tSheet, indexDescription, index);
		return tCellDescription;
	}

	protected TCell loadCellStatus(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(indexStatusTest);
		TCell tCellStatus = excelAdapter.getCell(tSheet, indexStatusTest, index);
		return tCellStatus;
	}

	protected boolean isIgnoredTest(int index) {
		return columnsIgnored.contains(index) //
				|| index == indexParameter //
				|| index == indexIo;
	}

	protected boolean isIgnoredParameter(int index) {
		return rowsIgnored.contains(index) //
				|| index == indexCode //
				|| index == indexActive //
				|| indexGroup != null && index == indexGroup //
				|| indexOrder != null && index == indexOrder //
				|| indexName != null && index == indexName //
				|| indexDescription != null && index == indexDescription;
	}

	protected boolean isLimitTest(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setColumn(index);
		return index <= tSheet.getRowMax();
	}

	protected boolean isLimitParameter(ExcelPosition excelPosition, TSheet tSheet, int index) {
		excelPosition.setRow(index);
		return index <= tSheet.getRowMax();
	}
}
