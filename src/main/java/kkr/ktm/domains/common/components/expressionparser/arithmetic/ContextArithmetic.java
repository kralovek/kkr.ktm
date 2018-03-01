package kkr.ktm.domains.common.components.expressionparser.arithmetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.expressionparser.ContextExpression;

public class ContextArithmetic implements ContextExpression {
	private static final Logger LOG = Logger.getLogger(ContextArithmetic.class);

	private Map<String, Object> parameters = new HashMap<String, Object>();

	public void checkValue(String parameter, Object value) {
		checkValue(parameter, value, null);
	}

	private void checkValue(String parameter, Object value, Collection<Integer> levels) {
		if (value == null) {
			throw new IllegalArgumentException("Value of parameter " + parameter + levels.toString() + " is NULL");
		}
		if (value instanceof Number) {
			return;
		}
		if (value.getClass().isArray()) {
			Collection<Integer> levelsCurrent = new ArrayList<Integer>();
			if (levels != null) {
				levelsCurrent.addAll(levels);
			}
			Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				levelsCurrent.add(i);
				checkValue(parameter, array[i], levelsCurrent);
			}
			return;
		}

		throw new IllegalArgumentException(
				"Value of parameter " + parameter + toStringLevels(levels) + " is not a number or array");
	}

	protected String toStringLevels(Collection<Integer> levels) {
		return toStringLevels(levels.toArray(new Integer[levels.size()]));
	}

	protected String toStringLevels(Integer[] levels) {
		if (levels == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (Integer level : levels) {
			buffer.append("[").append(level).append("]");
		}
		return buffer.toString();
	}

	public void addParameter(String name, Object value) {
		checkValue(name, value);
		parameters.put(name, value);
	}

	public void addParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			checkValue(entry.getKey(), entry.getValue());
		}
	}

	public void removeParameter(String name) {
		parameters.remove(name);
	}

	public boolean isParameter(String name) {
		return parameters.containsKey(name);
	}

	public Number getParameter(String name, Integer... indexes) throws IndexOutOfBoundsException {
		Object value = parameters.get(name);
		if (value == null) {
			return null;
		}

		Collection<Integer> availableIndexes = new ArrayList<Integer>();

		if (indexes != null) {
			for (int i = 0; i < indexes.length; i++) {
				if (!value.getClass().isArray()) {
					throw new IndexOutOfBoundsException("Awailable: " + name + toStringLevels(availableIndexes)
							+ " Requested: " + name + toStringLevels(indexes));
				}
				Object[] values = (Object[]) value;
				availableIndexes.add(values.length);
				if (indexes[i] >= values.length) {
					throw new IndexOutOfBoundsException("Awailable: " + name + toStringLevels(availableIndexes)
							+ " Requested: " + name + toStringLevels(indexes));
				}
				value = values[indexes[i]];
			}
		}

		if (value.getClass().isArray()) {
			Object[] values = (Object[]) value;
			availableIndexes.add(values.length);
			throw new IndexOutOfBoundsException("Awailable: " + name + toStringLevels(availableIndexes) + " Requested: "
					+ name + toStringLevels(indexes));
		}

		Number retval = (Number) value;
		return retval;
	}

	public static final void main(String[] argv) {
		LOG.trace("BEGIN");
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MYPAR", //
					new Object[] { //
							0, new Object[] { 1 }, //
							new Object[] { 11, 22 }, //
							new Object[] { 111, 222, 333 }, //
							new Object[] { 1111, 2222, 3333, 4444 } //
					});

			ContextArithmetic contextArithmetic = new ContextArithmetic();
			contextArithmetic.addParameters(parameters);

			Number number;
			// LOG.debug("Indexes: ");
			// number = contextArithmetic.getParameter("MYPAR");
			// LOG.debug("Result: " + number);
			LOG.debug("Indexes: 0");
			number = contextArithmetic.getParameter("MYPAR", 0);
			LOG.debug("Result: " + number);
			LOG.debug("Indexes: 1, 0");
			number = contextArithmetic.getParameter("MYPAR", 1, 0);
			LOG.debug("Result: " + number);
			LOG.debug("Indexes: 2, 1");
			number = contextArithmetic.getParameter("MYPAR", 2, 3);
			LOG.debug("Result: " + number);
			LOG.debug("Indexes: 3, 2");
			number = contextArithmetic.getParameter("MYPAR", 3, 2);
			LOG.debug("Result: " + number);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
