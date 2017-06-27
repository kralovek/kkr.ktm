package kkr.ktm.data;

import java.util.Map;

public interface TestInput extends Test {

	String getName();

	String getDescription();

    Map<String, Object> getDataInput();
}
