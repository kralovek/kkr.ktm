package kkr.ktm.domains.common.components.parametersformater.template.format;

public abstract class FormatBase {
	private FormatType type;

	FormatBase(FormatType type) {
		this.type = type;
	}

	public static Format newFormat(FormatType type, String format) {
		switch (type) {
		case DATE:
			return new FormatDate(format);
		case VALUE:
			return new FormatValue(format);
		case DEFAULT:
			return new FormatDefault();
		default:
			throw new IllegalArgumentException("Unsupported FormatType: " + type.name());
		}
	}

	public FormatType getType() {
		return type;
	}
}
