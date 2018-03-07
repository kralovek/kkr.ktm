package kkr.ktm.domains.common.components.formatter.bytype;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.FormatterFactory;

public abstract class FormatterFactoryByTypeFwk extends FormatterByTypeFwk {
	private boolean configured;

	protected FormatterFactory formatterFactoryAuto;
	protected FormatterFactory formatterFactoryDate;
	protected FormatterFactory formatterFactoryDecimal;
	protected FormatterFactory formatterFactoryInteger;
	protected FormatterFactory formatterFactoryBoolean;
	protected FormatterFactory formatterFactoryString;

	public void config() throws ConfigurationException {
		configured = false;

		if (formatterFactoryAuto == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'formatterFactoryAuto' is not configured");
		}
		if (formatterFactoryDate == null) {
			formatterFactoryDate = formatterFactoryAuto;
		}
		if (formatterFactoryDecimal == null) {
			formatterFactoryDecimal = formatterFactoryAuto;
		}
		if (formatterFactoryInteger == null) {
			formatterFactoryInteger = formatterFactoryAuto;
		}
		if (formatterFactoryBoolean == null) {
			formatterFactoryBoolean = formatterFactoryAuto;
		}
		if (formatterFactoryString == null) {
			formatterFactoryString = formatterFactoryAuto;
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public FormatterFactory getFormatterFactoryAuto() {
		return formatterFactoryAuto;
	}

	public void setFormatterFactoryAuto(FormatterFactory formatterFactoryAuto) {
		this.formatterFactoryAuto = formatterFactoryAuto;
	}

	public FormatterFactory getFormatterFactoryDate() {
		return formatterFactoryDate;
	}

	public void setFormatterFactoryDate(FormatterFactory formatterFactoryDate) {
		this.formatterFactoryDate = formatterFactoryDate;
	}

	public FormatterFactory getFormatterFactoryDecimal() {
		return formatterFactoryDecimal;
	}

	public void setFormatterFactoryDecimal(FormatterFactory formatterFactoryDecimal) {
		this.formatterFactoryDecimal = formatterFactoryDecimal;
	}

	public FormatterFactory getFormatterFactoryInteger() {
		return formatterFactoryInteger;
	}

	public void setFormatterFactoryInteger(FormatterFactory formatterFactoryInteger) {
		this.formatterFactoryInteger = formatterFactoryInteger;
	}

	public FormatterFactory getFormatterFactoryBoolean() {
		return formatterFactoryBoolean;
	}

	public void setFormatterFactoryBoolean(FormatterFactory formatterFactoryBoolean) {
		this.formatterFactoryBoolean = formatterFactoryBoolean;
	}

	public FormatterFactory getFormatterFactoryString() {
		return formatterFactoryString;
	}

	public void setFormatterFactoryString(FormatterFactory formatterFactoryString) {
		this.formatterFactoryString = formatterFactoryString;
	}
}
