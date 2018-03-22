package kkr.ktm.domains.tests.components.testreporter.excel;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import kkr.common.errors.ConfigurationException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.excel.components.catalogstyles.CatalogStylesFactory;
import kkr.ktm.domains.excel.components.exceladapter.ExcelAdapter;
import kkr.ktm.domains.excel.components.structureloader.StructureLoader;
import kkr.ktm.domains.excel.utils.UtilsExcel;
import kkr.ktm.domains.tests.components.valueformatter.ValueFormatter;
import kkr.ktm.domains.tests.components.valueparser.ValueParser;

public abstract class TestReporterExcelFwk {
	private boolean configured;

	protected ExcelAdapter excelAdapter;
	protected StructureLoader structureLoader;
	protected CatalogStylesFactory catalogStylesFactory;
	protected ValueParser valueParser;
	protected ValueFormatter valueFormatter;

	protected File dir;

	//
	// REVIEW
	//
	protected Boolean review;
	protected String reviewSheet;

	private Integer _reviewRowHeader;
	protected Integer reviewRowHeader;

	private Integer _reviewRowFirst;
	protected Integer reviewRowFirst;

	private String _reviewColumnName;
	protected Integer reviewColumnName;

	private String _reviewColumnStatusTotal;
	protected Integer reviewColumnStatusTotal;

	private String _reviewColumnStatusOk;
	protected Integer reviewColumnStatusOk;

	private String _reviewColumnStatusKo;
	protected Integer reviewColumnStatusKo;

	private String _reviewColumnStatusSkip;
	protected Integer reviewColumnStatusSkip;

	public void config() throws ConfigurationException {
		configured = false;

		if (excelAdapter == null) {
			throw new ConfigurationException("Parameter 'excelAdapter' is not configured");
		}
		if (structureLoader == null) {
			throw new ConfigurationException("Parameter 'structureLoader' is not configured");
		}
		if (valueParser == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'valueParser' is not configured");
		}
		if (valueFormatter == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'valueFormatter' is not configured");
		}
		if (catalogStylesFactory == null) {
			throw new ConfigurationException("Parameter 'catalogStylesFactory' is not configured");
		}

		if (dir == null) {
			throw new ConfigurationException("Parameter 'dir' is not configured");
		} else {
			try {
				dir = dir.getCanonicalFile();
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'dir' has bad value: " + dir);
			}
		}

		//
		// REVIEW
		//
		if (review == null) {
			review = false;
		}
		reviewRowHeader = UtilsExcel.adaptAndCheckRowId(getClass(), _reviewRowHeader, "reviewRowHeader");
		reviewRowFirst = UtilsExcel.adaptAndCheckRowId(getClass(), _reviewRowFirst, "reviewRowFirst");
		reviewColumnName = UtilsExcel.adaptAndCheckColumnId(getClass(), _reviewColumnName, "reviewColumnName");
		reviewColumnStatusTotal = UtilsExcel.adaptAndCheckColumnId(getClass(), _reviewColumnStatusTotal,
				"reviewColumnStatusTotal");
		reviewColumnStatusOk = UtilsExcel.adaptAndCheckColumnId(getClass(), _reviewColumnStatusOk,
				"reviewColumnStatusOk");
		reviewColumnStatusKo = UtilsExcel.adaptAndCheckColumnId(getClass(), _reviewColumnStatusKo,
				"reviewColumnStatusKo");
		reviewColumnStatusSkip = UtilsExcel.adaptAndCheckColumnId(getClass(), _reviewColumnStatusSkip,
				"reviewColumnStatusSkip");

		if (review) {
			if (UtilsString.isEmpty(reviewSheet)) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is activated and the parameter 'reviewSheet' is not configured");
			}

			if (reviewRowHeader == null) {
				// OK
			}

			if (reviewRowFirst == null) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is adtivated and the parameter 'reviewRowFirst' is not configured");
			}

			if (reviewColumnName == null) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is adtivated and the parameter 'reviewColumnName' is not configured");
			}

			if (reviewColumnStatusOk == null) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is adtivated and the parameter 'reviewColumnStatusOk' is not configured");
			}

			if (reviewColumnStatusKo == null) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is adtivated and the parameter 'reviewColumnStatusKo' is not configured");
			}

			if (reviewColumnStatusSkip == null) {
				throw new ConfigurationException(getClass().getSimpleName()
						+ ": Review is adtivated and the parameter 'reviewColumnStatusSkip' is not configured");
			}
		}

		Collection<Integer> rows = new HashSet<Integer>();
		UtilsExcel.checkDoubles(rows, _reviewRowHeader, "reviewRowHeader");
		UtilsExcel.checkDoubles(rows, _reviewRowFirst, "reviewRowFirst");

		Collection<String> columns = new HashSet<String>();
		UtilsExcel.checkDoubles(columns, _reviewColumnName, "reviewColumnName");
		UtilsExcel.checkDoubles(columns, _reviewColumnStatusTotal, "reviewColumnStatusTotal");
		UtilsExcel.checkDoubles(columns, _reviewColumnStatusOk, "reviewColumnStatusOk");
		UtilsExcel.checkDoubles(columns, _reviewColumnStatusKo, "reviewColumnStatusKo");
		UtilsExcel.checkDoubles(columns, _reviewColumnStatusSkip, "reviewColumnStatusSkip");

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public ExcelAdapter getExcelAdapter() {
		return excelAdapter;
	}

	public void setExcelAdapter(ExcelAdapter excelAdapter) {
		this.excelAdapter = excelAdapter;
	}

	public StructureLoader getStructureLoader() {
		return structureLoader;
	}

	public void setStructureLoader(StructureLoader structureLoader) {
		this.structureLoader = structureLoader;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public Integer getReviewRowFirst() {
		return _reviewRowFirst;
	}

	public void setReviewRowFirst(Integer reviewRowFirst) {
		this._reviewRowFirst = reviewRowFirst;
	}

	public Integer getReviewRowHeader() {
		return _reviewRowHeader;
	}

	public void setReviewRowHeader(Integer reviewRowHeader) {
		this._reviewRowHeader = reviewRowHeader;
	}

	public String getReviewColumnName() {
		return _reviewColumnName;
	}

	public void setReviewColumnName(String reviewColumnName) {
		this._reviewColumnName = reviewColumnName;
	}

	public String getReviewColumnStatusTotal() {
		return _reviewColumnStatusTotal;
	}

	public void setReviewColumnStatusTotal(String reviewColumnStatusTotal) {
		this._reviewColumnStatusTotal = reviewColumnStatusTotal;
	}

	public String getReviewColumnStatusOk() {
		return _reviewColumnStatusOk;
	}

	public void setReviewColumnStatusOk(String reviewColumnStatusOk) {
		this._reviewColumnStatusOk = reviewColumnStatusOk;
	}

	public String getReviewColumnStatusKo() {
		return _reviewColumnStatusKo;
	}

	public void setReviewColumnStatusKo(String reviewColumnStatusKo) {
		this._reviewColumnStatusKo = reviewColumnStatusKo;
	}

	public String getReviewColumnStatusSkip() {
		return _reviewColumnStatusSkip;
	}

	public void setReviewColumnStatusSkip(String reviewColumnStatusSkip) {
		this._reviewColumnStatusSkip = reviewColumnStatusSkip;
	}

	public Boolean getReview() {
		return review;
	}

	public void setReview(Boolean review) {
		this.review = review;
	}

	public String getReviewSheet() {
		return reviewSheet;
	}

	public void setReviewSheet(String reviewSheet) {
		this.reviewSheet = reviewSheet;
	}

	public CatalogStylesFactory getCatalogStylesFactory() {
		return catalogStylesFactory;
	}

	public void setCatalogStylesFactory(CatalogStylesFactory catalogStylesFactory) {
		this.catalogStylesFactory = catalogStylesFactory;
	}

	public ValueParser getValueParser() {
		return valueParser;
	}

	public void setValueParser(ValueParser valueParser) {
		this.valueParser = valueParser;
	}

	public ValueFormatter getValueFormatter() {
		return valueFormatter;
	}

	public void setValueFormatter(ValueFormatter valueFormatter) {
		this.valueFormatter = valueFormatter;
	}
}
