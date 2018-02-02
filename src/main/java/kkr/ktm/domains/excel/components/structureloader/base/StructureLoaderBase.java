package kkr.ktm.domains.excel.components.structureloader.base;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.ExcelException;
import kkr.common.utils.UtilsString;
import kkr.common.utils.excel.ExcelPosition;
import kkr.ktm.domains.excel.components.exceladapter.TCell;
import kkr.ktm.domains.excel.components.exceladapter.TSheet;
import kkr.ktm.domains.excel.components.exceladapter.TWorkbook;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.data.Active;
import kkr.ktm.domains.excel.data.Io;
import kkr.ktm.domains.excel.data.Orientation;
import kkr.ktm.domains.excel.data.Status;
import kkr.ktm.domains.excel.data.StructureParameter;
import kkr.ktm.domains.excel.data.StructureSheet;
import kkr.ktm.domains.excel.data.StructureTest;
import kkr.ktm.domains.excel.data.StructureWorkbook;

public abstract class StructureLoaderBase extends StructureLoaderBaseFwk implements StructureLoader {
	private static final Logger LOG = Logger.getLogger(StructureLoaderBase.class);

	public StructureWorkbook loadStructureWorkbook(ExcelPosition excelPositionWorkbook, TWorkbook tWorkbook) throws BaseException {
		LOG.trace("BEGIN");
		try {
			StructureWorkbookImpl structureWorkbook = new StructureWorkbookImpl(tWorkbook.getFile().getAbsolutePath());

			int sheetCount = excelAdapter.getSheetCount(tWorkbook);
			for (int i = 0; i < sheetCount; i++) {
				TSheet tSheet = excelAdapter.getSheet(tWorkbook, i);

				if (!selectionSheets.isSelected(tSheet.getName())) {
					continue;
				}

				StructureSheet structureSheet = loadStructureSheet(excelPositionWorkbook, tSheet);
				structureWorkbook.getSheets().put(structureSheet.getName(), structureSheet);
			}

			LOG.trace("OK");
			return structureWorkbook;
		} finally {
			LOG.trace("END");
		}
	}

	public StructureSheet loadStructureSheet(ExcelPosition excelPositionWorkbook, TSheet tSheet) throws BaseException {
		LOG.trace("BEGIN");
		try {
			StructureSheetImpl structureSheet = new StructureSheetImpl(tSheet.getName(), orientation());
			structureSheet.setIndexStatusTest(indexStatusTest);
			structureSheet.setIndexStatusSheetOk(cellStatusSheetOk);
			structureSheet.setIndexStatusSheetKo(cellStatusSheetKo);
			structureSheet.setIndexStatusSheetSkip(cellStatusSheetSkip);

			ExcelPosition excelPositionSheet = excelPositionWorkbook.clone();
			excelPositionSheet.setSheet(tSheet.getName());

			loadStructureTests(structureSheet, excelPositionSheet, tSheet);

			loadStructureParameters(structureSheet, excelPositionSheet, tSheet, Io.I);

			// O before E
			loadStructureParameters(structureSheet, excelPositionSheet, tSheet, Io.O);

			// E after O
			loadStructureParameters(structureSheet, excelPositionSheet, tSheet, Io.E);

			LOG.trace("OK");
			return structureSheet;
		} finally {
			LOG.trace("END");
		}
	}

	protected void loadStructureParameters(StructureSheet structureSheet, ExcelPosition excelPositionSheet, TSheet tSheet, Io io)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			ExcelPosition excelPosition = excelPositionSheet.clone();
			Collection<String> parameters = new HashSet<String>();

			for (int i = 0; isLimitParameter(excelPosition, tSheet, i); i++) {
				if (isIgnoredParameter(i)) {
					continue;
				}
				excelPosition.setColumn(i);

				//
				// IO
				//
				{
					TCell tCellIo = loadCellIo(excelPosition, tSheet, i);
					String valueIo = excelAdapter.getStringValue(tCellIo);
					if (UtilsString.isEmpty(valueIo)) {
						continue;
					}
					Io ioLoc;
					try {
						ioLoc = Io.valueOf(valueIo);
					} catch (Exception ex) {
						throw new ExcelException(excelPosition, "Bad value on the Io: '" + valueIo + "'");
					}
					if (ioLoc != io) {
						continue;
					}
				}

				StructureParameter structureParameter = new StructureParameter(i);
				structureParameter.setIo(io);

				//
				// PARAMETER
				//
				{
					TCell tCellParameter = loadCellParameter(excelPosition, tSheet, i);
					String valueParameter = excelAdapter.getStringValue(tCellParameter);
					if (UtilsString.isEmpty(valueParameter)) {
						throw new ExcelException(excelPosition, "The parameter name is empty");
					}

					structureParameter.setName(valueParameter);

					switch (io) {
						case E :
							StructureParameter structureParameterO = structureSheet.findParameterO(structureParameter.getName());
							if (structureParameterO == null) {
								throw new ExcelException(excelPosition, "The parameter E does not exist as parameter O: " + valueParameter);
							}
							// NO BREAK
						case I :
							if (parameters.contains(valueParameter)) {
								throw new ExcelException(excelPosition, "The parameter " + io + " is not unique: " + valueParameter);
							}
							break;
						default :
					}
					parameters.add(valueParameter);
					structureParameter.setName(valueParameter);
				}

				//
				// END
				//
				structureSheet.addParameter(structureParameter);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	protected void loadStructureTests(StructureSheet structureSheet, ExcelPosition excelPositionSheet, TSheet tSheet) throws BaseException {
		LOG.trace("BEGIN");
		try {
			ExcelPosition excelPosition = excelPositionSheet.clone();
			Set<String> codes = new HashSet<String>();
			Map<Integer, Collection<Integer>> orders = new HashMap<Integer, Collection<Integer>>();

			for (int i = 0; isLimitTest(excelPosition, tSheet, i); i++) {
				if (isIgnoredTest(i)) {
					continue;
				}

				//
				// ACTIVE
				//
				{
					TCell tCellActive = loadCellActive(excelPosition, tSheet, i);
					String valueActive = excelAdapter.getStringValue(tCellActive);
					if (UtilsString.isEmpty(valueActive)) {
						continue;
					}
					Active active;
					try {
						active = Active.valueOf(valueActive);
					} catch (Exception ex) {
						throw new ExcelException(excelPosition, "Bad value of the Active: '" + valueActive + "'");
					}
					if (active != Active.Y) {
						continue;
					}
				}

				StructureTest structureTest = new StructureTest(i);

				//
				// CODE
				//
				{
					TCell tCellCode = loadCellCode(excelPosition, tSheet, i);
					String valueCode = excelAdapter.getStringValue(tCellCode);
					if (UtilsString.isEmpty(valueCode)) {
						throw new ExcelException(excelPosition, "The Test Code is not specified");
					}

					if (codes.contains(valueCode)) {
						throw new ExcelException(excelPosition, "The test Code is not unique on the sheet: '" + valueCode + "'");
					}
					codes.add(valueCode);
					structureTest.setCode(valueCode);
				}

				//
				// STATUS
				//
				{
					TCell tCellStatus = loadCellStatus(excelPosition, tSheet, i);
					String valueStatus = excelAdapter.getStringValue(tCellStatus);
					if (UtilsString.isEmpty(valueStatus)) {
						structureTest.setStatus(null);
					} else {
						Status status;
						try {
							status = Status.valueOf(valueStatus);
							structureTest.setStatus(status);
						} catch (Exception ex) {
							throw new ExcelException(excelPosition, "Bad value on the DiffStatus: '" + valueStatus + "'");
						}
					}
				}

				//
				// GROUP
				//
				if (indexGroup != null) {
					TCell tCellGroup = loadCellGroup(excelPosition, tSheet, i);
					String valueGroup = excelAdapter.getStringValue(tCellGroup);
					if (!UtilsString.isEmpty(valueGroup)) {
						try {
							int group = Integer.parseInt(valueGroup);
							if (group <= 0) {
								throw new ExcelException(excelPosition, "The Group must be positive integer: '" + valueGroup + "'");
							}
							structureTest.setGroup(group);
						} catch (NumberFormatException ex) {
							throw new ExcelException(excelPosition, "The test Group is not a number: '" + valueGroup + "'");
						}
					}
				}

				//
				// NAME
				//
				if (indexName != null) {
					TCell tCellName = loadCellName(excelPosition, tSheet, i);
					String valueName = excelAdapter.getStringValue(tCellName);
					structureTest.setName(valueName);
				}

				//
				// DESCRIPTION
				//
				if (indexDescription != null) {
					TCell tCellDescription = loadCellDescription(excelPosition, tSheet, i);
					String valueDescription = excelAdapter.getStringValue(tCellDescription);
					structureTest.setName(valueDescription);
				}

				//
				// ORDER
				//
				if (indexOrder != null) {
					TCell tCellOrder = loadCellOrder(excelPosition, tSheet, i);
					String valueOrder = excelAdapter.getStringValue(tCellOrder);
					if (!UtilsString.isEmpty(valueOrder)) {
						try {
							int order = Integer.parseInt(valueOrder);
							if (order <= 0) {
								throw new ExcelException(excelPosition, "The Order must be positive integer: '" + valueOrder + "'");
							}

							Collection<Integer> groupOrders = orders.get(structureTest.getGroup());
							if (groupOrders == null) {
								groupOrders = new HashSet<Integer>();
								orders.put(structureTest.getGroup(), groupOrders);
							}

							if (groupOrders.contains(order)) {
								throw new ExcelException(excelPosition,
										"The test Order is not unique for the group "
												+ (structureTest.getGroup() != null ? structureTest.getGroup() : "AUTO") + " on the sheet: '"
												+ valueOrder + "'");
							}
							groupOrders.add(order);

							structureTest.setOrder(order);
						} catch (NumberFormatException ex) {
							throw new ExcelException(excelPosition, "The test Order is not a number: '" + valueOrder + "'");
						}
					}
				}

				//
				// END
				//
				structureSheet.addTest(structureTest);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	protected abstract TCell loadCellIo(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellParameter(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellActive(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellCode(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellGroup(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellOrder(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellName(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellDescription(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract TCell loadCellStatus(ExcelPosition excelPosition, TSheet tSheet, int index);

	protected abstract boolean isIgnoredTest(int index);
	protected abstract boolean isIgnoredParameter(int index);
	protected abstract boolean isLimitTest(ExcelPosition excelPosition, TSheet tSheet, int index);
	protected abstract boolean isLimitParameter(ExcelPosition excelPosition, TSheet tSheet, int index);

	protected abstract Orientation orientation();
}
