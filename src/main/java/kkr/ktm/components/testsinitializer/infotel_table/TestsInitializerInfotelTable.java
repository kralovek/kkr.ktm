package kkr.ktm.components.testsinitializer.infotel_table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.testsinitializer.TestsInitializer;
import kkr.ktm.data.TestInput;
import kkr.ktm.exception.BaseException;

public class TestsInitializerInfotelTable extends TestsInitializerInfotelTableFwk implements TestsInitializer {
	private static final Logger LOG = Logger.getLogger(TestsInitializerInfotelTable.class);

	private Map<String, Map<String, Object>> concatenateInputParametersByTestType(List<TestInput> testInputs) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, Map<String, Collection<Object>>> retvalCol = new LinkedHashMap<String, Map<String, Collection<Object>>>();

			// Prepare structures first.
			// Values will be put insight after - it's important
			// Because parameters may be missing for some tests
			for (TestInput testInput : testInputs) {
				Map<String, Collection<Object>> typeParameters = retvalCol.get(testInput.getType());
				if (typeParameters == null) {
					typeParameters = new LinkedHashMap<String, Collection<Object>>();
					retvalCol.put(testInput.getType(), typeParameters);
				}

				for (Map.Entry<String, Object> entry : testInput.getDataInput().entrySet()) {
					Collection<Object> values = typeParameters.get(entry.getKey());
					if (values == null) {
						values = new ArrayList<Object>();
						typeParameters.put(entry.getKey(), values);
					}
				}
			}

			// Now the values will be filled
			for (TestInput testInput : testInputs) {
				Map<String, Collection<Object>> typeParameters = retvalCol.get(testInput.getType());

				for (Map.Entry<String, Collection<Object>> entry : typeParameters.entrySet()) {
					Object value = testInput.getDataInput().get(entry.getKey());
					// mo matter the value is null or not
					Collection<Object> values = typeParameters.get(entry.getKey());
					values.add(value);
				}
			}

			// Transformation Collection -> Array
			Map<String, Map<String, Object>> retval = new LinkedHashMap<String, Map<String, Object>>();
			for (Map.Entry<String, Map<String, Collection<Object>>> entry : retvalCol.entrySet()) {
				Map<String, Object> typeParameters = new LinkedHashMap<String, Object>();
				retval.put(entry.getKey(), typeParameters);
				for (Map.Entry<String, Collection<Object>> entry2 : entry.getValue().entrySet()) {
					typeParameters.put(entry2.getKey(), entry2.getValue().toArray());
				}
			}

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public void initialize(List<TestInput> testInputs, Map<String, Object> commonData) throws BaseException {
		LOG.trace("BEGIN");
		try {
			if (commonData == null) {
				throw new IllegalArgumentException("CommonData Map may not be null");
			}

			InfotelDataContainer dataContainer = new InfotelDataContainer();

			commonData.put(keyData, dataContainer);

			Map<String, Map<String, Object>> typeParameters = concatenateInputParametersByTestType(testInputs);

			for (Map.Entry<String, Map<String, Object>> entry : typeParameters.entrySet()) {
				String type = entry.getKey();
				Map<String, Object> parameters = entry.getValue();
				workType(type, parameters, dataContainer);
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private void workType(String type, Map<String, Object> inputParameters, InfotelDataContainer dataContainer) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String queryTemplate = templateArchiv.loadTemplate(type);
			String query = formatterParameters.format(queryTemplate, inputParameters);

			Collection<Map<String, Object>> tableData = tableReader.readData(query);

			dataContainer.putTypeData(type, tableData);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
