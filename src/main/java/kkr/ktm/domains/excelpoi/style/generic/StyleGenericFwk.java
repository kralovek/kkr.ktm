package kkr.ktm.domains.excelpoi.style.generic;

import org.apache.poi.ss.usermodel.IndexedColors;

import kkr.ktm.domains.excelpoi.style.Alignment;
import kkr.ktm.domains.excelpoi.style.Boldweight;
import kkr.ktm.exception.ConfigurationException;

public abstract class StyleGenericFwk {
	private boolean configured;

	//	private static final short DEFAULT_BOLDWEIGHT = Font.BOLDWEIGHT_NORMAL;
	//	private static final short DEFAULT_ALIGNMENT = CellStyle.ALIGN_LEFT;
	//	private static final IndexedColors DEFAULT_BACKGROUND_COLOR = IndexedColors.BLACK;
	//	private static final IndexedColors DEFAULT_FOREGROUND_COLOR = IndexedColors.BLACK;

	protected String name;

	private String boldweight;
	protected Short poiBoldweight;

	private String alignment;
	protected Short poiAlignment;

	private String backgroundColor;
	protected IndexedColors poiBackgroundColor;

	private String foregroundColor;
	protected IndexedColors poiForegroundColor;

	public void config() throws ConfigurationException {
		configured = false;

		if (name == null) {
			throw new ConfigurationException("Parameter 'name' is not configured");
		}

		if (alignment == null) {
			poiAlignment = null;
		} else {
			try {
				Alignment enumAlignment = Alignment.valueOf(alignment);
				poiAlignment = enumAlignment.getValue();
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'alignment' has bad value: " + alignment);
			}
		}

		if (boldweight == null) {
			poiBoldweight = null;
		} else {
			try {
				Boldweight enumBoldweight = Boldweight.valueOf(boldweight);
				poiBoldweight = enumBoldweight.getValue();
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'boldweight' has bad value: " + alignment);
			}
		}

		if (backgroundColor == null) {
			poiBackgroundColor = null;
		} else {
			try {
				poiBackgroundColor = IndexedColors.valueOf(backgroundColor);
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'backgroundColor' has bad value: " + backgroundColor);
			}
		}

		if (foregroundColor == null) {
			poiForegroundColor = null;
		} else {
			try {
				poiForegroundColor = IndexedColors.valueOf(foregroundColor);
			} catch (Exception ex) {
				throw new ConfigurationException("Parameter 'foregroundColor' has bad value: " + backgroundColor);
			}
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBoldweight() {
		return boldweight;
	}

	public void setBoldweight(String boldweight) {
		this.boldweight = boldweight;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(String foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
}
