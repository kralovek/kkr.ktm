package kkr.ktm.domains.tests.components.testloader.excel;

import java.util.HashMap;
import java.util.Map;

import kkr.ktm.domains.tests.data.Test;
import kkr.ktm.domains.tests.data.TestBase;
import kkr.ktm.domains.tests.data.TestInput;

public class TestInputExcel extends TestBase implements TestInput, Comparable<TestInputExcel> {

	private Integer order;

	private Map<String, Object> dataInput = new HashMap<String, Object>();

	private int orderOfSheet;
	private int orderInSheet;

	public TestInputExcel(Test test) {
		super(test);
	}

	public TestInputExcel(String name, String description, String source, String type, String code, Integer group) {
		super(name, description, source, type, code, group);
	}

	public Map<String, Object> getDataInput() {
		return dataInput;
	}

	public int getOrderOfSheet() {
		return orderOfSheet;
	}

	public void setOrderOfSheet(int orderSheet) {
		this.orderOfSheet = orderSheet;
	}

	public int getOrderInSheet() {
		return orderInSheet;
	}

	public void setOrderInSheet(int orderInSheet) {
		this.orderInSheet = orderInSheet;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public int compareTo(TestInputExcel test) {
		int result = 0;
		//
		// NULL
		//
		if (test == null) {
			return -1;
		}

		//
		// SOURCE
		//
		result = source.compareTo(test.source);
		if (result != 0) {
			return result;
		}

		//
		// GROUP
		//
		result = compare(group, test.group);
		if (result != 0) {
			return result;
		}

		//
		// ORDER
		//
		result = compare(order, test.order);
		if (result != 0) {
			return result;
		}

		//
		// TYPE
		//
		result = new Integer(orderOfSheet).compareTo(test.orderOfSheet);
		if (result != 0) {
			return result;
		}

		//
		// COLUMN
		//
		result = Integer.valueOf(orderInSheet).compareTo(test.orderInSheet);
		return result;
	}

	public String toString() {
		return "IN " + super.toString() + " [ORDER: " + orderOfSheet + "~" + orderInSheet + "]";
	}

	private int compare(Integer i1, Integer i2) {
		if (i1 != null && i2 != null) {
			return i1.compareTo(i2);
		} else if (i1 == null) {
			return +1;
		} else if (i2 == null) {
			return -1;
		} else {
			return 0;
		}
	}
}
