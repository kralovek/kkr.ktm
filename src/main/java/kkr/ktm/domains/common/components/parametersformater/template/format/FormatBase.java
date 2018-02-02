package kkr.ktm.domains.common.components.parametersformater.template.format;

import kkr.ktm.domains.common.components.parametersformater.template.parts.TagParameter;

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
		case AUTO:
			if (format != null && !format.isEmpty()) {
				throw new IllegalArgumentException("When " + TagParameter.ATTR_FORMAT_TYPE + " is " + FormatType.AUTO
						+ " no pattern cannot be specified: " + format);
			}
			return new FormatAuto();
		default:
			throw new IllegalArgumentException("Unsupported FormatType: " + type.name());
		}
	}

	public FormatType getType() {
		return type;
	}
}
