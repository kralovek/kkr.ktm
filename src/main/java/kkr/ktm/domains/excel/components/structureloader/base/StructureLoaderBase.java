package kkr.ktm.domains.excel.components.structureloader.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	public StructureWorkbook loadStructureWorkbook(ExcelPosition excelPositionWorkbook, TWorkbook tWorkbook)
			throws BaseException {
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
		LOG.trace("BEGIN: " + tSheet.getName());
		try {
			StructureSheetImpl structureSheet = new StructureSheetImpl(tSheet.getName(), orientation());
			structureSheet.setIndexStatusSheetOk(cellStatusSheetOk);
			structureSheet.setIndexStatusSheetKo(cellStatusSheetKo);
			structureSheet.setIndexStatusSheetSkip(cellStatusSheetSkip);

			ExcelPosition excelPositionSheet = excelPositionWorkbook.clone();
			excelPositionSheet.setSheet(tSheet.getName());

			loadStructureParameters(structureSheet, excelPositionSheet, tSheet);

			loadStructureTests(structureSheet, excelPositionSheet, tSheet);

			LOG.trace("OK");
			return structureSheet;
		} finally {
			LOG.trace("END");
		}
	}

	protected void loadStructureParameters(StructureSheet structureSheet, ExcelPosition excelPositionSheet,
			TSheet tSheet) throws BaseException {
		LOG.trace("BEGIN");
		try {
			ExcelPosition excelPosition = excelPositionSheet.clone();
			Set<SystemParameter> systemParameters = new HashSet<SystemParameter>();

			for (int i = 0; isLimitParameter(tSheet, i); i++) {
				if (isIgnoredParameter(i)) {
					continue;
				}

				//
				// IO
				//
				Io io;
				{
					TCell tCellIo = loadCell(tSheet, i, indexIo);
					String valueIo = excelAdapter.getStringValue(tCellIo);
					if (UtilsString.isEmpty(valueIo)) {
						continue;
					}
					try {
						io = Io.valueOf(valueIo);
					} catch (Exception ex) {
						updateExcelPosition(excelPosition, i, indexIo);
						throw new ExcelException(excelPosition, "Bad value on the Io: '" + valueIo + "'");
					}
				}

				StructureParameter structureParameter = new StructureParameter(i);
				structureParameter.setIo(io);

				//
				// PARAMETER
				//
				{
					updateExcelPosition(excelPosition, i, indexParameter);
					TCell tCellParameter = loadCell(tSheet, i, indexParameter);
					String valueParameter = excelAdapter.getStringValue(tCellParameter);
					if (UtilsString.isEmpty(valueParameter)) {
						throw new ExcelException(excelPosition, "The parameter name is empty");
					}

					structureParameter.setName(valueParameter);

					switch (io) {
					case S:
						try {
							SystemParameter systemParameter = SystemParameter.valueOf(valueParameter);
							if (systemParameters.contains(systemParameter)) {
								throw new ExcelException(excelPosition,
										"The parameter " + io + " is not unique: " + valueParameter);
							}
							systemParameters.add(systemParameter);
							structureSheet.addSystemParameter(systemParameter, i);
						} catch (ExcelException ex) {
							throw ex;
						} catch (Exception ex) {
							throw new ExcelException(excelPosition, "Unsupported system parameter: " + valueParameter);
						}
						continue;
					case E:
						if (structureSheet.findParameterE(structureParameter.getName()) != null) {
							throw new ExcelException(excelPosition,
									"The parameter " + io + " is not unique: " + valueParameter);
						}
						break;
					case I:
						if (structureSheet.findParameterI(structureParameter.getName()) != null) {
							throw new ExcelException(excelPosition,
									"The parameter " + io + " is not unique: " + valueParameter);
						}
						break;
					default:
					}
				}

				//
				// END
				//
				structureSheet.addParameter(structureParameter);
			}

			//
			// VERIFICATIONS
			//
			Iterator<StructureParameter> iterator = structureSheet.iteratorParametersE();
			while (iterator.hasNext()) {
				StructureParameter structureParameterE = iterator.next();
				if (structureSheet.findParameterO(structureParameterE.getName()) == null) {
					updateExcelPosition(excelPosition, structureParameterE.getIndex(), indexParameter);
					throw new ExcelException(excelPosition,
							"The parameter O does not exist for parameter E: " + structureParameterE.getName());
				}
			}

			if (structureSheet.getIndexActive() == null) {
				throw new ExcelException(excelPositionSheet,
						"Missing sthe system parameter " + SystemParameter.ACTIVE + " which is mandatory");
			}
			if (structureSheet.getIndexCode() == null) {
				throw new ExcelException(excelPositionSheet,
						"Missing sthe system parameter " + SystemParameter.CODE + " which is mandatory");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	protected abstract void updateExcelPosition(ExcelPosition excelPosition, int indexParameter, int index);

	protected void loadStructureTests(StructureSheet structureSheet, ExcelPosition excelPositionSheet, TSheet tSheet)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			ExcelPosition excelPosition = excelPositionSheet.clone();
			Set<String> codes = new HashSet<String>();
			Map<Integer, Collection<Integer>> orders = new HashMap<Integer, Collection<Integer>>();

			for (int i = 0; isLimitTest(tSheet, i); i++) {
				if (isIgnoredTest(i)) {
					continue;
				}

				try {
					//
					// ACTIVE
					//
					{
						updateExcelPosition(excelPosition, structureSheet.getIndexActive(), i);
						TCell tCellActive = loadCell(tSheet, structureSheet.getIndexActive(), i);
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
						updateExcelPosition(excelPosition, structureSheet.getIndexCode(), i);
						TCell tCellCode = loadCell(tSheet, structureSheet.getIndexCode(), i);
						String valueCode = excelAdapter.getStringValue(tCellCode);
						if (UtilsString.isEmpty(valueCode)) {
							throw new ExcelException(excelPosition, "The Test Code is not specified");
						}

						if (codes.contains(valueCode)) {
							throw new ExcelException(excelPosition,
									"The test Code is not unique on the sheet: '" + valueCode + "'");
						}
						codes.add(valueCode);
						structureTest.setCode(valueCode);
					}

					//
					// STATUS
					//
					if (structureSheet.getIndexStatus() != null) {
						updateExcelPosition(excelPosition, structureSheet.getIndexStatus(), i);
						TCell tCellStatus = loadCell(tSheet, structureSheet.getIndexStatus(), i);
						String valueStatus = excelAdapter.getStringValue(tCellStatus);
						if (UtilsString.isEmpty(valueStatus)) {
							structureTest.setStatus(null);
						} else {
							Status status;
							try {
								status = Status.valueOf(valueStatus);
								structureTest.setStatus(status);
							} catch (Exception ex) {
								throw new ExcelException(excelPosition,
										"Bad value on the Status: '" + valueStatus + "'");
							}
						}
					}

					//
					// GROUP
					//
					if (structureSheet.getIndexGroup() != null) {
						updateExcelPosition(excelPosition, structureSheet.getIndexGroup(), i);
						TCell tCellGroup = loadCell(tSheet, structureSheet.getIndexGroup(), i);
						String valueGroup = excelAdapter.getStringValue(tCellGroup);
						if (!UtilsString.isEmpty(valueGroup)) {
							try {
								int group = Integer.parseInt(valueGroup);
								if (group <= 0) {
									throw new ExcelException(excelPosition,
											"The Group must be positive integer: '" + valueGroup + "'");
								}
								structureTest.setGroup(group);
							} catch (NumberFormatException ex) {
								throw new ExcelException(excelPosition,
										"The test Group is not a number: '" + valueGroup + "'");
							}
						}
					}

					//
					// NAME
					//
					if (structureSheet.getIndexName() != null) {
						updateExcelPosition(excelPosition, structureSheet.getIndexName(), i);
						TCell tCellName = loadCell(tSheet, structureSheet.getIndexName(), i);
						String valueName = excelAdapter.getStringValue(tCellName);
						structureTest.setName(valueName);
					}

					//
					// DESCRIPTION
					//
					if (structureSheet.getIndexDescription() != null) {
						updateExcelPosition(excelPosition, structureSheet.getIndexDescription(), i);
						TCell tCellDescription = loadCell(tSheet, structureSheet.getIndexDescription(), i);
						String valueDescription = excelAdapter.getStringValue(tCellDescription);
						structureTest.setName(valueDescription);
					}

					//
					// ORDER
					//
					if (structureSheet.getIndexOrder() != null) {
						updateExcelPosition(excelPosition, structureSheet.getIndexOrder(), i);
						TCell tCellOrder = loadCell(tSheet, structureSheet.getIndexOrder(), i);
						String valueOrder = excelAdapter.getStringValue(tCellOrder);
						if (!UtilsString.isEmpty(valueOrder)) {
							try {
								int order = Integer.parseInt(valueOrder);
								if (order <= 0) {
									throw new ExcelException(excelPosition,
											"The Order must be positive integer: '" + valueOrder + "'");
								}

								Collection<Integer> groupOrders = orders.get(structureTest.getGroup());
								if (groupOrders == null) {
									groupOrders = new HashSet<Integer>();
									orders.put(structureTest.getGroup(), groupOrders);
								}

								if (groupOrders.contains(order)) {
									throw new ExcelException(excelPosition,
											"The test Order is not unique for the group "
													+ (structureTest.getGroup() != null ? structureTest.getGroup()
															: "AUTO")
													+ " on the sheet: '" + valueOrder + "'");
								}
								groupOrders.add(order);

								structureTest.setOrder(order);
							} catch (NumberFormatException ex) {
								throw new ExcelException(excelPosition,
										"The test Order is not a number: '" + valueOrder + "'");
							}
						}
					}
					//
					// END
					//
					structureSheet.addTest(structureTest);
				} catch (ExcelException ex) {
					throw ex;
				} catch (BaseException ex) {
					throw new ExcelException(excelPosition, "Unexpected problem", ex);
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	protected abstract TCell loadCell(TSheet tSheet, int indexParameter, int index);

	protected abstract Orientation orientation();

	protected boolean isIgnoredTest(int index) {
		return indexesTestsIgnored.contains(index) //
				|| index == indexParameter //
				|| index == indexIo;
	}

	protected boolean isIgnoredParameter(int index) {
		return indexesParametersIgnored.contains(index);
	}

	protected boolean isLimitTest(TSheet tSheet, int indexCurrentTest) {
		return indexCurrentTest <= tSheet.getRowMax();
	}

	protected boolean isLimitParameter(TSheet tSheet, int indexCurrentParameter) {
		return indexCurrentParameter <= tSheet.getColumnMax();
	}

}
