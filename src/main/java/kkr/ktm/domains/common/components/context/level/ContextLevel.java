package kkr.ktm.domains.common.components.context.level;

import java.util.HashMap;
import java.util.Map;

import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.context.Context;
import kkr.ktm.domains.common.components.parametersformater.template.value.UtilsValue;

public class ContextLevel implements Context {
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public Object getParameter(String name, Integer... indexValues) throws IndexOutOfBoundsException {
		if (!parameters.containsKey(name)) {
			throw new IllegalArgumentException("Unknown parameter: " + name);
		}

		Object object = parameters.get(name);

		Object objectLevel = getLevel(name, object, indexValues);

		if (!UtilsValue.isValidValue(objectLevel)) {
			throw new IllegalArgumentException("The parameter " + name + toStringIndexes(indexValues, null)
					+ " does not contain a scalar of allowed type: " + objectLevel);
		}

		return objectLevel;
	}

	public int getParameterSize(String name, Integer... indexValues) {
		if (!parameters.containsKey(name)) {
			throw new IllegalArgumentException("Unknown parameter: " + name);
		}

		Object object = parameters.get(name);

		Object level = getLevel(name, object, indexValues);

		if (level == null || !level.getClass().isArray()) {
			return 1;
		}

		Object[] array = (Object[]) level;
		return array.length;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters.clear();
		this.parameters.putAll(parameters);
	}

	public Object getLevel(String name, Object object, Integer[] indexValues) throws IndexOutOfBoundsException {
		Object current = object;
		if (indexValues != null && indexValues.length > 0) {
			Integer[] awailable = new Integer[indexValues.length];

			for (int iLevel = 0; iLevel < indexValues.length; iLevel++) {
				int index = indexValues[iLevel];
				if (current == null || !current.getClass().isArray()) {
					if (index != 0) {
						throw new IndexOutOfBoundsException(
								"Not enough values for the parameter " + name + toStringIndexes(indexValues, iLevel)
										+ ". Available: " + toStringIndexes(awailable, iLevel));
					}
					if (iLevel + 1 != indexValues.length) {
						throw new IndexOutOfBoundsException(
								"Not enough values for the parameter " + name + toStringIndexes(indexValues, iLevel + 1)
										+ ". Available: " + toStringIndexes(awailable, iLevel));
					}
					break;
				} else {
					Object[] array = (Object[]) current;
					if (index < array.length) {
						current = array[index];
					} else if (index == 0) {
						current = "";
					} else {
						throw new IndexOutOfBoundsException(
								"Not enough values of the parameter " + name + toStringIndexes(indexValues, iLevel));
					}
				}
			}
		}
		return current;
	}

	private String toStringIndexes(Integer[] indexes, Integer maxLevel) {
		if (indexes != null) {
			if (maxLevel != null && maxLevel < indexes.length) {
				Integer[] indexesReduced = new Integer[maxLevel];
				for (int i = 0; i < indexesReduced.length; i++) {
					indexesReduced[i] = indexes[i];
				}
				return UtilsString.arrayToString(indexesReduced, "[", "]", ",");
			}
			return UtilsString.arrayToString(indexes, "[", "]", ",");
		} else {
			return "";
		}
	}
}
