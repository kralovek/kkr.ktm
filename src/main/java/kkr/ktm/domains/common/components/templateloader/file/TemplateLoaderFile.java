package kkr.ktm.domains.common.components.templateloader.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.errors.ConfigurationException;
import kkr.common.errors.TechnicalException;
import kkr.ktm.domains.common.components.templateloader.TemplateLoader;

public class TemplateLoaderFile extends TemplateLoaderFileFwk implements TemplateLoader {
	private static final Logger LOG = Logger.getLogger(TemplateLoaderFile.class);

	private static final String EXTENSION = ".tpl";

	public String loadTemplate(String name) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			File fileTemplate = null;
			for (File dir : dirs) {
				fileTemplate = createFilepath(dir, name);
				if (fileTemplate.isFile()) {
					break;
				} else {
					fileTemplate = null;
				}
			}
			if (fileTemplate == null) {
				throw new ConfigurationException("No template found for the code: " + name);
			}
			LOG.debug("Loading the template: " + fileTemplate);
			String retval = loadContent(fileTemplate);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private String loadContent(File file) throws BaseException {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			char[] buffer = new char[(int) file.length()];
			fileReader.read(buffer);
			String content = new String(buffer);
			fileReader.close();
			fileReader = null;
			return content;
		} catch (final FileNotFoundException ex) {
			throw new ConfigurationException("Template file does not exist: " + file.getAbsolutePath());
		} catch (final IOException ex) {
			throw new TechnicalException("Cannot read the template file: " + file.getAbsolutePath());
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (final IOException ex) {
				}
			}
		}
	}

	private File createFilepath(File dir, String code) {
		if (mapping != null) {
			for (Map.Entry<Pattern, String> entry : mapping.entrySet()) {
				if (entry.getKey().matcher(code).matches()) {
					return new File(dir, entry.getValue());
				}
			}
		}
		return new File(dir, code + EXTENSION);
	}
}
