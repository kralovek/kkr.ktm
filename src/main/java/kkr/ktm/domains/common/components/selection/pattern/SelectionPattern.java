package kkr.ktm.domains.common.components.selection.pattern;

import java.util.regex.Pattern;

import kkr.ktm.domains.common.components.selection.Selection;
import kkr.ktm.exception.BaseException;

public class SelectionPattern extends SelectionPatternFwk implements Selection {

	public boolean isSelected(String name) throws BaseException {
		boolean retval = false;
		boolean result = false;
		for (Pattern pattern : patterns) {
			if (pattern.matcher(name).matches()) {
				result = true;
				break;
			}
		}
		if (result) {
			for (Pattern exceptPattern : exceptPatterns) {
				if (exceptPattern.matcher(name).matches()) {
					result = false;
					break;
				}
			}
		}
		retval = mode == AcceptMode.INCLUDE ? result : !result;
		return retval;
	}

}
