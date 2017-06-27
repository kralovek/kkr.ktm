package kkr.ktm.batchs.convertormttktm;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.FunctionalException;

public class BatchConvertorMttKtm extends BatchConvertorMttKtmFwk {
	private static final Logger LOG = Logger.getLogger(BatchConvertorMttKtm.class);

	private static final String TAG_REGEXP = "###RegExp:";

	private static final FileFilter FILE_FILTER_XLSX = new FileFilter() {

		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xlsx");
		}
	};

	public void runConvertor() throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (!dir.isDirectory()) {
				throw new FunctionalException("The directory does not exist: " + dir.getAbsolutePath());
			}

			File[] files = dir.listFiles(FILE_FILTER_XLSX);

			for (File file : files) {
				workFile(file);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workFile(File file) throws BaseException {
		LOG.trace("BEGIN: " + file.getAbsolutePath());
		try {
			String type = file.getName().substring(0, file.getName().length() - 5);
			Collection<Map<String, Object>> readData = tableReader.readData(type);

			Collection<String> readHeader = buildReadHeader(readData);

			List<Collection<Object>> writeData = buildWriteHeader(readHeader);

			buildWriteLines(readHeader, readData, writeData);

			Collection<String> writeHeader = buildWriteHeader(writeData);

			List<Collection<Object>> writeDataSpaces = addWriteDataSpaces(writeHeader, writeData);

			tableWriter.writeData(type, writeHeader, writeDataSpaces);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private List<Collection<Object>> addWriteDataSpaces(Collection<String> writeHeader, List<Collection<Object>> writeData) {
		List<Collection<Object>> retval = new ArrayList<Collection<Object>>();

		for (Collection<Object> writeLine : writeData) {
			retval.add(writeLine);
			Collection<Object> emptyLine = new ArrayList<Object>();
			retval.add(emptyLine);

			for (Object column : writeLine) {
				if (emptyLine.isEmpty()) {
					emptyLine.add(column.toString());
				} else {
					emptyLine.add(null);
				}
			}
		}

		return retval;
	}

	private Collection<String> buildWriteHeader(List<Collection<Object>> writeData) throws BaseException {
		if (writeData.isEmpty()) {
			throw new FunctionalException("Cannot extract the header from the data, there is no data");
		}
		Collection<Object> headerObject = writeData.remove(0);

		Collection<String> headerString = new ArrayList<String>();

		int i = 0;
		for (Object object : headerObject) {
			if (object == null) {
				throw new IllegalArgumentException("Header [" + i + "] may not contain null value");
			}
			i++;
			headerString.add(object.toString());
		}

		return headerString;
	}

	private Collection<String> buildReadHeader(Collection<Map<String, Object>> lines) {
		LOG.trace("BEGIN");
		try {

			Collection<String> retval = new LinkedHashSet<String>();

			for (Map<String, Object> line : lines) {
				retval.addAll(line.keySet());
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Object convertValue(Object value) {
		if (value == null || !(value instanceof String)) {
			return value;
		}
		String retval = (String) value;

		if (retval.startsWith(TAG_REGEXP)) {
			retval = retval.substring(TAG_REGEXP.length());
			retval = "{" + retval.replace("[", "\\[").replace("]", "\\]") + "}";
		}

		return retval;
	}

	private void buildWriteLines(Collection<String> readHeader, Collection<Map<String, Object>> readData, List<Collection<Object>> writeData) {
		LOG.trace("BEGIN");
		try {
			for (Map<String, Object> readLine : readData) {
				int i = 0;
				for (String readColumn : readHeader) {
					Object readValue = readLine.get(readColumn);
					Object writeValue = convertValue(readValue);
					Collection<Object> writeLine = writeData.get(i);
					writeLine.add(writeValue);
					i++;
				}
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private List<Collection<Object>> buildWriteHeader(Collection<String> readHeader) {
		LOG.trace("BEGIN");
		try {
			List<Collection<Object>> writeData = new ArrayList<Collection<Object>>();

			for (String column : readHeader) {
				Collection<Object> line = new ArrayList<Object>();
				writeData.add(line);
				line.add(column);
			}

			LOG.trace("OK");
			return writeData;
		} finally {
			LOG.trace("END");
		}
	}
}
