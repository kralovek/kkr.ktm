package kkr.ktm.components.lancer;

import java.util.Map;

import kkr.ktm.data.TestInput;
import kkr.ktm.data.TestOutput;
import kkr.ktm.exception.BaseException;

/**
 * TestLancer
 *
 * @author KRALOVEC-99999
 */
public interface Lancer {

	TestOutput lance(TestInput testInput, Map<String, Object> commonData) throws BaseException;
}
