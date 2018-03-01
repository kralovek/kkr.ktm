package kkr.ktm.domains.common.components.parametersformater.template;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.expressionparser.arithmetic.ContextArithmetic;

public class ContextNumberExpression extends ContextArithmetic {
	private static final Logger LOG = Logger.getLogger(ContextNumberExpression.class);
	private Map<String, Integer> currentIndexes = new HashMap<String, Integer>();

	public ContextNumberExpression(Map<String, Object> parameters) {
		LOG.trace("BEGIN");
		try {
			LOG.debug("Filtered parameters: ");
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				Object valueConv = convertValue(entry.getValue());
				if (valueConv != null) {
					super.addParameter(entry.getKey(), valueConv);
				} else {
					LOG.debug(" - " + entry.getKey());
				}
			}
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	private Object convertValue(Object valueOrg) {
		if (valueOrg == null || valueOrg instanceof Number) {
			return valueOrg;
		}

		if (valueOrg.getClass().isArray()) {
			Object[] valuesOrg = (Object[]) valueOrg;
			Object[] valuesConv = new Object[valuesOrg.length];
			for (int i = 0; i < valuesOrg.length; i++) {
				Object valueConv = convertValue(valuesOrg[i]);
				if (valueConv == null) {
					return null;
				}
				valuesConv[i] = valueConv;
			}
			return valuesConv;
		}

		if (valueOrg instanceof String) {
			try {
				return Double.parseDouble((String) valueOrg);
			} catch (NumberFormatException ex) {
				return null;
			}
		}

		return null;
	}

	public void addParameter(String name, Object value) {
		throw new IllegalStateException("Do not use the method " + this.getClass().getSimpleName() + ".addParameter");
	}

	public void addParameters(Map<String, Object> parameters) {
		throw new IllegalStateException("Do not use the method " + this.getClass().getSimpleName() + ".addParameters");
	}

	public Number getParameter(String name, Integer... indexes) throws IndexOutOfBoundsException {
		if (currentIndexes != null) {
			Integer index = currentIndexes.get(name);
			if (index != null) {
				if (indexes != null && indexes.length != 0) {
					throw new IndexOutOfBoundsException(
							"Index parameter must be requested always as scalar: " + name + toStringLevels(indexes));
				}
				return index;
			}
		}
		return super.getParameter(name, indexes);
	}

	public void updateIndex(String name, int value) throws IllegalArgumentException {
		if (!currentIndexes.containsKey(name)) {
			throw new IllegalArgumentException("Index was not initialized, so it cannot be updated: " + name);
		}
		currentIndexes.put(name, value);
	}

	public void addIndex(String name, int value) throws IllegalArgumentException {
		if (isParameter(name)) {
			throw new IllegalArgumentException("Conflict in the name of the index and an existing parameter: " + name);
		}
		if (currentIndexes.containsKey(name)) {
			throw new IllegalArgumentException(
					"Conflict in the name of the index and an existing parent index: " + name);
		}
		currentIndexes.put(name, value);
	}

	public void removeIndex(String name) {
		currentIndexes.remove(name);
	}

	public void setIndexes(Map<String, Integer> indexes) throws IllegalArgumentException {
		if (indexes != null) {
			for (Map.Entry<String, Integer> entry : indexes.entrySet()) {
				if (isParameter(entry.getKey())) {
					throw new IllegalArgumentException(
							"Conflict in the name of existing parameter and index: " + entry.getKey());
				}
				if (entry.getValue() == null) {
					throw new IllegalArgumentException("Value of the index is NULL: " + entry.getKey());
				}
			}
			currentIndexes = indexes;
		} else {
			currentIndexes.clear();
		}
	}
}
