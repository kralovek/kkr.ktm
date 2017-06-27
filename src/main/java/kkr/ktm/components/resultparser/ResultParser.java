package kkr.ktm.components.resultparser;


import java.util.Map;

import kkr.ktm.exception.BaseException;

/**
 * ResultParser
 *
 * @author KRALOVEC-99999
 */
public interface ResultParser {
    Map<String, Object> parse(final String pSource) throws BaseException;
}
