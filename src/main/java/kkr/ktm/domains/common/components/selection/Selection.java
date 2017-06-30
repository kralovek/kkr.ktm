package kkr.ktm.domains.common.components.selection;

import kkr.common.errors.BaseException;

public interface Selection {

	boolean isSelected(String name) throws BaseException;
}
