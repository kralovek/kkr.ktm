package kkr.ktm.domains.common.components.selection;

import kkr.ktm.exception.BaseException;

public interface Selection {

	boolean isSelected(String name) throws BaseException;
}
