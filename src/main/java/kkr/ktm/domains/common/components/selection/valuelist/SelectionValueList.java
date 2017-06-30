package kkr.ktm.domains.common.components.selection.valuelist;

import kkr.ktm.domains.common.components.selection.Selection;
import kkr.common.errors.BaseException;

public class SelectionValueList extends SelectionValueListFwk implements Selection {

	public boolean isSelected(String name) throws BaseException {
		return values.contains(name);
	}
}
