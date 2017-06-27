package kkr.ktm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import kkr.ktm.exception.ConfigurationException;

public class UtilsConfig {
	public static Collection<Pattern> checkListPatterns(Collection<String> masks, String name) throws ConfigurationException {
		Collection<Pattern> patterns = new ArrayList<Pattern>();
		if (masks != null) {
			patterns = new ArrayList<Pattern>();
			int i = 1;
			for (String mask : masks) {
				if (mask == null) {
					throw new ConfigurationException("Parameter '" + name + "' has bad value [" + i + "]: " + mask);
				}
				try {
					Pattern pattern = Pattern.compile(mask);
					patterns.add(pattern);
				} catch (Exception ex) {
					throw new ConfigurationException("Parameter '" + name + "' has bad value [" + i + "]: " + mask, ex);
				}
				i++;
			}
		}
		return patterns;
	}
}
