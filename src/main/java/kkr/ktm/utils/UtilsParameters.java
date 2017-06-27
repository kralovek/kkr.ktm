package kkr.ktm.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kkr.ktm.exception.ConfigurationException;
import kkr.ktm.exception.FunctionalException;

public class UtilsParameters {

	public static String getStringParam(final Map<String, Object> inputParameters,
			String parameter) throws FunctionalException {
		Object object = inputParameters.get(parameter);
		if (object == null) {
			throw new FunctionalException("The parameter " + parameter
					+ " is not specified");
		}
		if (object.getClass().isArray()) {
			Object[] objectArray = (Object[]) object;
			if (objectArray.length == 0) {
				throw new FunctionalException("The parameter " + parameter
						+ " is not specified");
			}
			object = objectArray[0];
		}
		if (!(object instanceof String)) {
			throw new FunctionalException("The parameter " + parameter
					+ " must be a string");
		}
		return (String) object;
	}

	public static Boolean getStringBoolean(final Map<String, Object> inputParameters,
			String parameter, boolean obligatory) throws FunctionalException {
		Object object = inputParameters.get(parameter);
		if (object == null) {
			if (obligatory) {
				throw new FunctionalException("The parameter " + parameter
						+ " is not specified");
			} else {
				return false;
			}
		}
		if (object.getClass().isArray()) {
			Object[] objectArray = (Object[]) object;
			if (objectArray.length == 0) {
				if (obligatory) {
					throw new FunctionalException("The parameter " + parameter
							+ " is not specified");
				} else {
					return false;
				}
			}
			object = objectArray[0];
		}

		if (object instanceof String) {
			return new Boolean((String) object);
		} else if (object instanceof Boolean) {
			return (Boolean) object;
		} else {
			throw new FunctionalException("The parameter " + parameter
					+ " must be a string or a boolean");
		}
	}

	public static <T> Map<Pattern, T> toByTypePattern(Class<?> clazz, Map<String, T> mapByType) throws ConfigurationException {
		Map<Pattern, T> mapByTypePattern = new LinkedHashMap<Pattern, T>();
		for (Map.Entry<String, T> entry : mapByType.entrySet()) {
			try {
				Pattern pattern = Pattern.compile(entry.getKey());
				mapByTypePattern.put(pattern, entry.getValue());
			} catch (PatternSyntaxException ex) {
				throw new ConfigurationException(clazz.getSimpleName()
						+ ": The key of the map lancerByType has bad value: "
						+ entry.getKey() + " reason: " + ex.getMessage());
			}
		}
		return mapByTypePattern;
	}
	
	public static <T> T chooseByTypePattern(T def, Map<Pattern, T> mapByTypePattern, String key) {
		if (mapByTypePattern == null) {
			return  def;
		}
		for (Map.Entry<Pattern, T> entry : mapByTypePattern.entrySet()) {
			Pattern pattern = entry.getKey();
			if (pattern.matcher(key).matches()) {
				return entry.getValue();
			}
		}
		return def;
	}
}
