package kkr.ktm.components.lancer.template;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kkr.ktm.components.executant.Executant;
import kkr.ktm.components.resultparser.ResultParser;
import kkr.ktm.components.templatearchiv.TemplateArchiv;
import kkr.ktm.domains.common.components.formaterparameters.FormatterParameters;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

/**
 * LancerTemplateBasedFwk
 * 
 * @author KRALOVEC-99999
 */
public abstract class LancerTemplateBasedFwk {
	private boolean configured;

	protected TemplateArchiv templateArchiv;

	protected FormatterParameters formatterParameters;

	protected Executant executant;

	protected Map<String, Executant> executantByType;

	protected ResultParser resultParser;

	protected File dirTrace;

	protected Boolean traceSource;

	protected Boolean traceResult;

	protected Integer repeatUnsuccessful;

	public void config() throws BaseException {
		configured = false;
		if (traceSource == null) {
			traceSource = false;
		}
		if (traceResult == null) {
			traceResult = false;
		}
		if ((traceSource || traceResult) && dirTrace == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter dirTrace is not configured");
		}
		if (repeatUnsuccessful == null) {
			repeatUnsuccessful = 1;
		} else if (repeatUnsuccessful < 1) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter repeatUnsuccessful must be 1 or more");
		}
		if (executant == null && (executantByType == null || executantByType.isEmpty())) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Neither parameter executant nor executantByType are configured");
		}
		if (executantByType == null) {
			executantByType = new HashMap<String, Executant>();
		}
		if (resultParser == null) {
			throw new ConfigurationException(getClass().getSimpleName() + ": Parameter resultParser is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(this.getClass().getName()).append("]\n");
		buffer.append("    ").append("templateArchiv").append("=").append(templateArchiv == null ? "" : templateArchiv.getClass().getName())
				.append("\n");
		buffer.append("    ").append("formatterParameters").append("=")
				.append(formatterParameters == null ? "" : formatterParameters.getClass().getName()).append("\n");
		buffer.append("    ").append("executant").append("=").append(executant == null ? "" : executant.getClass().getName()).append("\n");
		buffer.append("    ").append("executants").append(":").append("\n");
		if (executantByType != null) {
			for (Map.Entry<String, Executant> entry : executantByType.entrySet()) {
				buffer.append("    ").append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue().getClass().getName()).append("\n");
			}
		}
		buffer.append("    ").append("resultParser").append("=").append(resultParser == null ? "" : resultParser.getClass().getName()).append("\n");
		buffer.append("    ").append("dirTrace").append("=").append(dirTrace == null ? "" : dirTrace).append("\n");
		buffer.append("    ").append("traceSource").append("=").append(traceSource == null ? "" : traceSource).append("\n");
		buffer.append("    ").append("traceResult").append("=").append(traceResult == null ? "" : traceResult).append("\n");
		buffer.append("    ").append("repeatUnsuccessful").append("=").append(repeatUnsuccessful == null ? "" : repeatUnsuccessful).append("\n");
		return buffer.toString();
	}

	public TemplateArchiv getTemplateArchiv() {
		return templateArchiv;
	}

	public void setTemplateArchiv(TemplateArchiv pTemplateArchiv) {
		this.templateArchiv = pTemplateArchiv;
	}

	public FormatterParameters getFormatterParameters() {
		return formatterParameters;
	}

	public void setFormatterParameters(FormatterParameters formatterParameters) {
		this.formatterParameters = formatterParameters;
	}

	public Executant getExecutant() {
		return executant;
	}

	public void setExecutant(Executant pExecutant) {
		this.executant = pExecutant;
	}

	public Map<String, Executant> getExecutantByType() {
		return executantByType;
	}

	public void setExecutantByType(Map<String, Executant> executantByType) {
		this.executantByType = executantByType;
	}

	public ResultParser getResultParser() {
		return resultParser;
	}

	public void setResultParser(ResultParser pResultParser) {
		this.resultParser = pResultParser;
	}

	public File getDirTrace() {
		return dirTrace;
	}

	public void setDirTrace(File pDir) {
		this.dirTrace = pDir;
	}

	public Boolean getTraceSource() {
		return traceSource;
	}

	public void setTraceSource(Boolean traceSource) {
		this.traceSource = traceSource;
	}

	public Boolean getTraceResult() {
		return traceResult;
	}

	public void setTraceResult(Boolean traceResult) {
		this.traceResult = traceResult;
	}

	public Integer getRepeatUnsuccessful() {
		return repeatUnsuccessful;
	}

	public void setRepeatUnsuccessful(Integer repeatUnsuccessful) {
		this.repeatUnsuccessful = repeatUnsuccessful;
	}
}
