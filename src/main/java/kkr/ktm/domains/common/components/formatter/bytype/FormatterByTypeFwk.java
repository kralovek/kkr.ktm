package kkr.ktm.domains.common.components.formatter.bytype;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.formatter.Formatter;

public abstract class FormatterByTypeFwk {
	private boolean configured;

	protected Formatter formatterDate;
	protected Formatter formatterDecimal;
	protected Formatter formatterInteger;
	protected Formatter formatterBoolean;
	protected Formatter formatterString;
	protected Formatter formatterAuto;

	public void config() throws ConfigurationException {
		configured = false;
		if (formatterAuto == null) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter 'formatterAuto' is not configured");
		}
		if (formatterBoolean == null) {
			formatterBoolean = formatterAuto;
		}
		if (formatterDate == null) {
			formatterDate = formatterAuto;
		}
		if (formatterDecimal == null) {
			formatterDecimal = formatterAuto;
		}
		if (formatterInteger == null) {
			formatterInteger = formatterAuto;
		}
		if (formatterString == null) {
			formatterString = formatterAuto;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Formatter getFormatterDate() {
		return formatterDate;
	}

	public void setFormatterDate(Formatter formatterDate) {
		this.formatterDate = formatterDate;
	}

	public Formatter getFormatterDecimal() {
		return formatterDecimal;
	}

	public void setFormatterDecimal(Formatter formatterDecimal) {
		this.formatterDecimal = formatterDecimal;
	}

	public Formatter getFormatterInteger() {
		return formatterInteger;
	}

	public void setFormatterInteger(Formatter formatterInteger) {
		this.formatterInteger = formatterInteger;
	}

	public Formatter getFormatterBoolean() {
		return formatterBoolean;
	}

	public void setFormatterBoolean(Formatter formatterBoolean) {
		this.formatterBoolean = formatterBoolean;
	}

	public Formatter getFormatterString() {
		return formatterString;
	}

	public void setFormatterString(Formatter formatterString) {
		this.formatterString = formatterString;
	}

	public Formatter getFormatterAuto() {
		return formatterAuto;
	}

	public void setFormatterAuto(Formatter formatterAuto) {
		this.formatterAuto = formatterAuto;
	}
}
