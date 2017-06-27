package kkr.ktm.components.lancer.infotel_table;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.lancer.Lancer;
import kkr.ktm.components.testsinitializer.infotel_table.InfotelDataContainer;
import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.FunctionalException;

public class LancerInfotelTable extends LancerInfotelTableFwk implements Lancer {
	private static final Logger LOG = Logger.getLogger(LancerInfotelTable.class);

	public TestOutput lance(TestInput testInput, Map<String, Object> commonData) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Object object = commonData.get(keyData);
			if (object == null) {
				throw new FunctionalException("The data container is not found in the commonData: " + keyData);
			}
			if (!(object instanceof InfotelDataContainer)) {
				throw new FunctionalException("The data container found in the commonData is not an instance of type: "
						+ InfotelDataContainer.class.getName() + " but: " + object.getClass().getName());
			}

			InfotelDataContainer infotelDataContainer = (InfotelDataContainer) object;

			Collection<Map<String, Object>> typeData = infotelDataContainer.getTypeData(testInput.getType());
			if (typeData == null) {
				throw new FunctionalException("The data container is not found for the type: " + testInput.getType());
			}

			Map<String, Object> line = findLine(testInput, typeData);

			TestOutput retval = new TestOutputImpl(testInput.getSource(), testInput.getType(), testInput.getId(), line);

			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private boolean isEmpty(Object value) {
		return value == null || value instanceof String && ((String) value).isEmpty();
	}

	private boolean compareValues(Object value1, Object value2) {
		if (isEmpty(value1) && isEmpty(value2)) {
			return true;
		}
		if (isEmpty(value1) || isEmpty(value2)) {
			return false;
		}

		if (value1.equals(value2)) {
			return true;
		}
		return value1.toString().equals(value2.toString());
	}

	private Map<String, Object> findLine(TestInput testInput, Collection<Map<String, Object>> dataTable) {
		LOG.trace("BEGIN");
		try {
			Map<String, Object> retval = null;
			int ir = 0;
			for (Map<String, Object> dataLine : dataTable) {
				if (dataLine == null) {
					throw new IllegalArgumentException("The line may not be null: " + ir);
				}

				boolean equal = true;
				for (String key : keys) {
					Object valueData = dataLine.get(key);
					Object valueInput = testInput.getDataInput().get(key);
					equal = compareValues(valueInput, valueData);
					if (!equal) {
						break;
					}
				}

				if (equal) {
					retval = dataLine;
					break;
				}
				ir++;
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}
}
