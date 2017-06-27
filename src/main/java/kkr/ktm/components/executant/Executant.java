package kkr.ktm.components.executant;

import kkr.ktm.exception.BaseException;

/**
 * Executant
 *
 * @author KRALOVEC-99999
 */
public interface Executant {

    String execute(final String pSource) throws BaseException;
}
