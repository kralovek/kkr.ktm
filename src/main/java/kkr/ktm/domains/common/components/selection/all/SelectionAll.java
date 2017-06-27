package kkr.ktm.domains.common.components.selection.all;

import kkr.ktm.domains.common.components.selection.Selection;
import kkr.ktm.exception.BaseException;

public class SelectionAll implements Selection {

	public boolean isSelected(String name) throws BaseException {
		return true;
	}
}
