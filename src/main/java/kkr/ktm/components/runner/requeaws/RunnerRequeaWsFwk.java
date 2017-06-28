package kkr.ktm.components.runner.requeaws;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import kkr.ktm.components.resultparser.ResultParser;
import kkr.ktm.components.templatearchiv.TemplateArchiv;
import kkr.ktm.domains.common.components.formaterparameters.FormatterParameters;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

public abstract class RunnerRequeaWsFwk {
	private static final String ENCODE_UTF_8 = "UTF-8";

	private boolean configured;

	protected URL url;
	protected String encoding;

	protected String templateHeadName;
	protected String templateBodyName;

	protected TemplateArchiv templateArchiv;

	protected FormatterParameters formatterParameters;

	protected ResultParser resultParser;

	protected Map<String, String> parameters;

	private String traceRequestFile;
	protected DateFormat traceRequestPattern;
	private String traceResponseFile;
	protected DateFormat traceResponsePattern;

	protected String sysParamPrefix;

	public void config() throws BaseException {
		configured = false;
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		if (url == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter url is not configured");
		} else if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) {
			throw new ConfigurationException(
					getClass().getSimpleName() + ": Parameter url contains unsupported protocol (supported are http|https): " + url.getProtocol());
		}
		if (templateHeadName == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter templateHeadName is not configured");
		}
		if (templateBodyName == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter templateBodyName is not configured");
		}
		if (formatterParameters == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter formatterParameters is not configured");
		}

		if (resultParser == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter resultParser is not configured");
		}

		if (traceRequestFile != null) {
			try {
				traceRequestPattern = new SimpleDateFormat(traceRequestFile);
			} catch (Exception ex) {
				throw new ConfigurationException(getClass().getSimpleName() + ": Parameter traceRequestFile has bad value: " + ex.getMessage());
			}
		}
		if (traceResponseFile != null) {
			try {
				traceResponsePattern = new SimpleDateFormat(traceResponseFile);
			} catch (Exception ex) {
				throw new ConfigurationException(getClass().getSimpleName() + ": Parameter traceResponseFile has bad value: " + ex.getMessage());
			}
		}

		if (encoding == null) {
			encoding = ENCODE_UTF_8;
		}
		if (sysParamPrefix == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter sysPrefix is not configured");
		} else if (!sysParamPrefix.isEmpty() && !sysParamPrefix.endsWith("/")) {
			sysParamPrefix += "/";
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public FormatterParameters getTemplateParser() {
		return formatterParameters;
	}

	public void setTemplateParser(FormatterParameters formatterParameters) {
		this.formatterParameters = formatterParameters;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public TemplateArchiv getTemplateArchiv() {
		return templateArchiv;
	}

	public void setTemplateArchiv(TemplateArchiv templateArchiv) {
		this.templateArchiv = templateArchiv;
	}

	public String getTemplateHeadName() {
		return templateHeadName;
	}

	public void setTemplateHeadName(String templateHeadName) {
		this.templateHeadName = templateHeadName;
	}

	public String getTemplateBodyName() {
		return templateBodyName;
	}

	public void setTemplateBodyName(String templateBodyName) {
		this.templateBodyName = templateBodyName;
	}

	public ResultParser getResultParser() {
		return resultParser;
	}

	public void setResultParser(ResultParser resultParser) {
		this.resultParser = resultParser;
	}

	public String getTraceRequestFile() {
		return traceRequestFile;
	}

	public void setTraceRequestFile(String traceRequestFile) {
		this.traceRequestFile = traceRequestFile;
	}

	public String getTraceResponseFile() {
		return traceResponseFile;
	}

	public void setTraceResponseFile(String traceResponseFile) {
		this.traceResponseFile = traceResponseFile;
	}

	public String getSysParamPrefix() {
		return sysParamPrefix;
	}

	public void setSysParamPrefix(String sysParamPrefix) {
		this.sysParamPrefix = sysParamPrefix;
	}
}
