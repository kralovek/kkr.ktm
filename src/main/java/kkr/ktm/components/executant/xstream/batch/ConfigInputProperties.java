package kkr.ktm.components.executant.xstream.batch;

import java.io.File;

import kkr.ktm.exception.ConfigurationException;



public class ConfigInputProperties {
    private String className;
    private File dirOutput;

    public ConfigInputProperties(final String[] pArgs) throws ConfigurationException {
        init(pArgs);
    }

    public String getClassName() {
		return className;
	}
	public File getDirOutput() {
		return dirOutput;
	}

    private void init(final String[] pArgs) throws ConfigurationException {
        if (pArgs.length % 2 != 0) {
            throw new ConfigurationException("A command-line parameter is missing a value");
        }
        for (int i = 0; i < pArgs.length; i += 2) {
            if (pArgs[i + 1].isEmpty()) {
                throw new ConfigurationException("Value of the parameter '" + pArgs[i] + "' is empty");
            }
            if ("className".equals(pArgs[i])) {
            	className = pArgs[i + 1];
            }
            if ("dir".equals(pArgs[i])) {
            	dirOutput = new File(pArgs[i + 1]);
            }
        }

        if (className == null) {
            throw new ConfigurationException("Config className is not specified");
        }
        if (dirOutput == null) {
            throw new ConfigurationException("Excel dir is not specified");
        }
    }
}
