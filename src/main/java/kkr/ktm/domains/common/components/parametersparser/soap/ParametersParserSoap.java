package kkr.ktm.domains.common.components.parametersparser.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersparser.ParametersParser;
import kkr.ktm.exception.BaseException;

public class ParametersParserSoap extends ParametersParserSoapFwk implements ParametersParser {
	private static final Logger LOG = Logger.getLogger(ParametersParserSoap.class);

	private static final String PARAM_PROTOCOL = "SOAP/PROTOCOL";
	private static final String PARAM_CODE = "SOAP/CODE";
	private static final String PARAM_STATUS = "SOAP/STATUS";

	public Map<String, Object> parse(String source) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Map<String, Object> parameters = new HashMap<String, Object>();

			final String cleanedContent = cleanContent(source);
			final String xmlContent = extractXmlContent(cleanedContent);
			final String header = extractHeader(cleanedContent);

			Map<String, String> parametersHeader = parseHeader(header);
			if (parametersHeader != null) {
				parameters.putAll(parametersHeader);
			}

			Map<String, Object> parametersXml = super.parse(xmlContent);
			if (parametersXml != null) {
				parameters.putAll(parametersXml);
			}

			LOG.trace("OK");
			return parameters;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, String> parseHeader(String header) {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] lines = header.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (i == 0) {
				Map<String, String> parametersStatus = parseHeaderStatus(line);
				parameters.putAll(parametersStatus);
			}
			if (line.length() == 0) {
				continue;
			}
			if (line.indexOf(':') < 1) {
				continue;
			}
			String[] keyVal = parseHeaderParameter(line);
			if (keyVal == null) {
				continue;
			}
			parameters.put(keyVal[0], keyVal[1]);
		}
		return parameters;
	}

	private String extractHeader(String content) {
		int iPos = content.indexOf("<?xml");
		if (iPos != -1) {
			return content.substring(0, iPos).replace("\r", "");
		} else {
			return content.replace("\r", "");
		}
	}

	private Map<String, String> parseHeaderStatus(String line) {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] parts = line.split(" ");
		if (parts.length >= 1) {
			parameters.put(PARAM_PROTOCOL, parts[0]);
		}
		if (parts.length >= 2) {
			parameters.put(PARAM_CODE, parts[1]);
		}
		if (parts.length >= 3) {
			parameters.put(PARAM_STATUS, parts[2]);
		}
		return parameters;
	}

	private String[] parseHeaderParameter(String line) {
		int iPos = line.indexOf(':');
		String[] retval = new String[2];
		retval[0] = line.substring(0, iPos).trim();
		if (iPos + 1 < line.length()) {
			retval[1] = line.substring(iPos + 1).trim();
		} else {
			retval[1] = "";
		}
		if (retval[0].isEmpty()) {
			return null;
		}
		return retval;
	}

	private String cleanContent(final String source) {
		if (source == null) {
			return null;
		}
		// final byte[] bytes = new byte[] {0x0D, 0x0A, 0x30, 0x37, 0x64, 0x61,
		// 0x0D, 0x0A};
		final byte[] bytes = new byte[]{0x0D, 0x0A, 0, 0, 0, 0, 0x0D, 0x0A};
		final byte[] sourceBytes = source.getBytes();

		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		for (int i = 0; i < sourceBytes.length;) {
			if (sourceBytes[i] == bytes[0]) {
				boolean found = true;
				for (int j = 0; j < bytes.length && i + j < sourceBytes.length; j++) {
					if (bytes[j] == 0) {
						continue;
					}
					if (sourceBytes[i + j] != bytes[j]) {
						found = false;
						break;
					}
				}
				if (found) {
					i += bytes.length;
					continue;
				}
			}
			try {
				buffer.write(new byte[]{sourceBytes[i]});
			} catch (final IOException ex) {
				i++;
				continue;
			}
			i++;
		}

		final byte[] cleanBytes = buffer.toByteArray();
		final String cleanedString = new String(cleanBytes);

		return cleanedString;
	}

	private String extractXmlContent(final String pResult) {
		if (pResult == null) {
			return null;
		}
		int pos1 = pResult.indexOf("<?xml");
		final int pos2 = pResult.lastIndexOf(">");
		if (pos1 != -1) {
			if (pos2 == -1 || pos1 > pos2) {
				return null;
			}
			return pResult.substring(pos1, pos2 + 1);
		}

		LOG.warn("Content does not contain <?xml...> !!! Trying to find a XML body");

		pos1 = pResult.indexOf("<");
		if (pos1 == -1 || pos2 == -1 || pos1 > pos2) {
			return null;
		}

		return pResult.substring(pos1, pos2 + 1);
	}
}
