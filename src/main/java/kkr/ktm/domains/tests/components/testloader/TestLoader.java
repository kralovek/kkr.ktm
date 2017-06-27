package kkr.ktm.domains.tests.components.testloader;

import java.util.Collection;

import kkr.ktm.domains.tests.data.TestInput;
import kkr.ktm.exception.BaseException;

public interface TestLoader {
	Collection<TestInput> loadTests(String source) throws BaseException;
}
