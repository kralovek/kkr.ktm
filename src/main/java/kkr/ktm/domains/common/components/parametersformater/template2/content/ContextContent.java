package kkr.ktm.domains.common.components.parametersformater.template2.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersformater.template.ContextNumberExpression;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueBase;

public class ContextContent {
	private static final Logger LOG = Logger.getLogger(ContextContent.class);
	private ContextNumberExpression contextExpression;
	private Map<String, Object> parameters;
	private Map<String, Integer> currentIndexes = new HashMap<String, Integer>();

	public ContextContent(Map<String, Object> parameters) {
		LOG.trace("BEGIN");
		try {
			contextExpression = new ContextNumberExpression(parameters);
			this.parameters = parameters;
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public int getParameterSize(String name, String... indexes) {
		if (!parameters.containsKey(name)) {
			throw new IndexOutOfBoundsException("Unknown parameter: " + name);
		}
		int[] indexesValues = evaluateIndexes(indexes);

		Object object = parameters.get(name);

		if (object != null) {
			Object objectLevel = retrieveObjectLevel(name, object, indexesValues);

			if (objectLevel.getClass().isArray()) {
				return ((Object[]) objectLevel).length;
			}
		}
		return 1;
	}

	public Value getParameter(String name, String... indexes)
			throws IllegalArgumentException, IndexOutOfBoundsException {
		if (!parameters.containsKey(name)) {
			throw new IllegalArgumentException("Unknown parameter: " + name);
		}

		int[] indexesValues = evaluateIndexes(indexes);

		Object object = parameters.get(name);

		Object objectLevel = retrieveObjectLevel(name, object, indexesValues);

		try {
			Value value = ValueBase.newValue(objectLevel);
			return value;
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(
					"The parameter " + name + toStringIndexes(indexesValues) + " must contain a scalar of allowed type",
					ex);
		}
	}

	private String toStringIndexes(int[] indexes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indexes.length; i++) {
			buffer.append("[").append(indexes[i]).append("]");
		}
		return buffer.toString();
	}

	private Object retrieveObjectLevel(String name, Object object, int[] indexes) throws IndexOutOfBoundsException {
		Object retval = null;
		Object objectCurrent = object;
		if (indexes != null && indexes.length > 0) {
			for (int index : indexes) {
				if (index == 0) {
					if (objectCurrent == null) {
						objectCurrent = "";
					}
					if (objectCurrent.getClass().isArray()) {
						Object[] array = (Object[]) objectCurrent;
						if (array.length == 0) {
							retval = objectCurrent = "";
						} else {
							retval = objectCurrent = array[0];
						}
					} else {
						retval = objectCurrent;
						objectCurrent = null;
					}
				} else {
					Object[] array;
					if (false //
							|| objectCurrent == null //
							|| !objectCurrent.getClass().isArray() //
							|| index >= (array = (Object[]) objectCurrent).length //
					) {
						throw new IndexOutOfBoundsException("Not enough values of the parameter " + name
								+ " for the index: " + toStringIndexes(indexes));
					}
					retval = objectCurrent = array[index];
				}
			}
			if (retval == null) {
				throw new IndexOutOfBoundsException(
						"Not enough values of the parameter " + name + " for the index: " + toStringIndexes(indexes));
			}
		} else {
			return object;
		}
		return retval;
	}

	protected int[] evaluateIndexes(String[] indexNames) throws IllegalArgumentException {
		int[] values = new int[indexNames != null ? indexNames.length : 0];
		for (int i = 0; i < values.length; i++) {
			Integer value = currentIndexes.get(indexNames[i]);
			if (value == null) {
				throw new IllegalArgumentException("Unknown index name: " + indexNames[i]);
			}
			values[i] = value;
		}
		return values;
	}

	public ContextNumberExpression getContextExpression() {
		return contextExpression;
	}

	public void updateIndex(String name, int value) throws IllegalArgumentException {
		if (!currentIndexes.containsKey(name)) {
			throw new IllegalArgumentException("Index was not initialized, so it cannot be updated: " + name);
		}
		currentIndexes.put(name, value);
		contextExpression.updateIndex(name, value);
	}

	public void addIndex(String name, int value) throws IllegalArgumentException {
		if (parameters.containsKey(name)) {
			throw new IllegalArgumentException("Conflict in the name of the index and an existing parameter: " + name);
		}
		if (currentIndexes.containsKey(name)) {
			throw new IllegalArgumentException(
					"Conflict in the name of the index and an existing parent index: " + name);
		}
		currentIndexes.put(name, value);
		contextExpression.addIndex(name, value);
	}

	public void removeIndex(String name) {
		currentIndexes.remove(name);
		contextExpression.removeIndex(name);
	}
}
