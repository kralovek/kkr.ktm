package kkr.ktm.domains.common.components.parametersparser.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.parametersparser.ParametersParser;
import kkr.ktm.exception.BaseException;
import kkr.ktm.utils.collections.OrderFiFoMap;
import kkr.ktm.utils.xml.Attribute;
import kkr.ktm.utils.xml.Tag;

/**
 * ParametersParserXml
 * 
 * @author KRALOVEC-99999
 */
public class ParametersParserXml extends ParametersParserXmlFwk implements ParametersParser {
	private static final Logger LOG = Logger.getLogger(ParametersParserXml.class);

	private static final String PARAM_EXCEPTION_CLASS = "EXCEPTION/CLASS";

	private static final String PARAM_EXCEPTION_MESSAGE = "EXCEPTION/MESSAGE";

	private static final String PARAM_EXCEPTION_DETAIL = "EXCEPTION/DETAIL";

	private static final String PATH_SEPARATOR = "/";

	private static final String OS_WINDOWS_NAME = "Windows";

	private class Value {
		private int[] index;
		private String path;
		private String prefix;
		private String value;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int[] getIndex() {
			return index;
		}

		public void setIndex(int[] index) {
			this.index = index;
		}

		public String getValue() {
			return value;
		}

		public void setValue(final String pValue) {
			if (pValue == null) {
				prefix = null;
				value = null;
			} else {
				int iPos = pValue.indexOf(':');
				if (iPos == -1 || iPos == 0) {
					prefix = null;
					value = pValue;
				} else {
					if (pValue.length() == iPos + 1) {
						value = "";
					} else {
						value = pValue.substring(iPos + 1);
					}
					prefix = pValue.substring(0, iPos);
					if (!isName(prefix)) {
						prefix = null;
						value = pValue;
					}
				}
			}
		}

		private boolean isName(final String pValue) {
			for (int i = 0; i < pValue.length(); i++) {
				if (i == 0) {
					if (!Character.isJavaIdentifierStart(pValue.charAt(i))) {
						return false;
					}
				} else {
					if (!Character.isJavaIdentifierPart(pValue.charAt(i))) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Map<String, Object> parse(final String pSource) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Map<String, Object> parameters = new OrderFiFoMap<String, Object>();

			if (pSource == null) {
				LOG.warn("No XML body");
				LOG.trace("OK");
				return parameters;
			}

			try {
				final Tag tag = createXmlTag(pSource, encoding);

				final Map<String, List<Value>> mapValues = createMapValues(null, tag, null);
				Map<String, Object> mapObjectsValues = listValuesToTreeValues(mapValues);
				parameters.putAll(mapObjectsValues);
			} catch (final XMLStreamException ex) {
				parameters.put(sysParamPrefix + PARAM_EXCEPTION_CLASS, ex.getClass().getSimpleName());
				parameters.put(sysParamPrefix + PARAM_EXCEPTION_MESSAGE, ex.getMessage());
				parameters.put(sysParamPrefix + PARAM_EXCEPTION_DETAIL, toStringException(ex));
			}

			LOG.trace("OK");
			return parameters;
		} finally {
			LOG.trace("END");
		}
	}

	private String toStringException(final Exception pException) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(byteArrayOutputStream);
		pException.printStackTrace(printStream);
		printStream.close();
		return byteArrayOutputStream.toString();
	}

	private Tag createXmlTag(final String pSource, final String pEncoding) throws XMLStreamException {
		XMLStreamReader xmlStreamReader = null;
		String osName = System.getProperty("os.name");
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pSource.getBytes());
		// On ne force l'encodage que si la machine est une machine windows 
		//(cf probl�me d'encodage sur le proxi : machine linux : pas besoin d'encodage)
		try {
			if (pEncoding != null && osName.contains(OS_WINDOWS_NAME)) {
				xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(byteArrayInputStream, pEncoding);
			} else {
				xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(byteArrayInputStream);
			}

			final Tag tag = readMainTag(xmlStreamReader);

			xmlStreamReader.close();
			xmlStreamReader = null;

			return tag;
		} finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				} catch (final XMLStreamException ex) {
				}
			}
		}
	}

	private Tag readMainTag(final XMLStreamReader pXmlStreamReader) throws XMLStreamException {
		final List<Tag> tags = new ArrayList<Tag>();

		Tag mainTag = null;
		int code;
		while ((code = pXmlStreamReader.next()) != XMLStreamConstants.END_DOCUMENT) {
			switch (code) {
				case XMLStreamConstants.START_ELEMENT :
					final Tag tag = readTag(pXmlStreamReader);
					if (mainTag != null) {
						tags.get(tags.size() - 1).getTags().add(tag);
					} else {
						mainTag = tag;
					}
					tags.add(tag);
					break;
				case XMLStreamConstants.CHARACTERS :
					final String currentValue = tags.get(tags.size() - 1).getValue();
					tags.get(tags.size() - 1).setValue((currentValue == null ? "" : currentValue) + pXmlStreamReader.getText());
					break;
				case XMLStreamConstants.END_ELEMENT :
					tags.remove(tags.size() - 1);
					break;
			}
		}

		return mainTag;
	}

	private Map<String, List<Value>> createMapValues(Map<String, List<Value>> pMapValues, final Tag pTag, Value pValue) {
		if (pMapValues == null) {
			pMapValues = new OrderFiFoMap<String, List<Value>>();
		}

		if (pValue == null) {
			pValue = new Value();
			pValue.setIndex(new int[]{0});
			pValue.setPath(useTagPrefix ? pTag.getComplexName() : pTag.getName());
			pValue.setValue(pTag.getValue());
		}

		int i = 0;

		if (pTag.getAttributes() != null && !pTag.getAttributes().isEmpty()) {
			for (final Attribute attribute : pTag.getAttributes()) {
				final Value value = new Value();
				value.setIndex(extendIndex(pValue.getIndex(), i++));
				value.setPath(pValue.getPath() + PATH_SEPARATOR + (useAttributePrefix ? attribute.getComplexName() : attribute.getName()));
				value.setValue(attribute.getValue());
				addValue(pMapValues, value);
			}
		}
		if (pTag.getTags() != null && !pTag.getTags().isEmpty()) {
			for (final Tag tag : pTag.getTags()) {
				final Value value = new Value();
				value.setIndex(extendIndex(pValue.getIndex(), i++));
				value.setPath(pValue.getPath() + PATH_SEPARATOR + (useTagPrefix ? tag.getComplexName() : tag.getName()));
				value.setValue(tag.getValue());
				//addValue(pMapValues, value);
				createMapValues(pMapValues, tag, value);
			}
		} else {
			addValue(pMapValues, pValue);
		}

		return pMapValues;
	}

	private void addValue(Map<String, List<Value>> pMapValues, final Value pValue) {
		if (pValue == null) {
			return;
		}
		if (pValue.getValue() == null) {
			pValue.setValue("");
		}
		List<Value> values = pMapValues.get(pValue.getPath());
		if (values == null) {
			values = new ArrayList<Value>();
			pMapValues.put(pValue.getPath(), values);
		}
		values.add(pValue);
	}

	private int[] extendIndex(final int[] pIndex, final int pTagIndex) {
		int[] index = new int[pIndex.length + 1];
		int i = 0;
		for (; i < pIndex.length; i++) {
			index[i] = pIndex[i];
		}
		index[i] = pTagIndex;
		return index;
	}

	private Tag readTag(final XMLStreamReader pXmlStreamReader) {
		final Tag tag = new Tag();
		tag.setPrefix(pXmlStreamReader.getPrefix());
		tag.setName(pXmlStreamReader.getLocalName());

		int attributeCount = pXmlStreamReader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			final Attribute attribute = createAttribute(pXmlStreamReader, i);
			tag.getAttributes().add(attribute);
		}
		return tag;
	}

	private Attribute createAttribute(final XMLStreamReader xmlStreamReader, final int pIndex) {
		final Attribute attribute = new Attribute();
		attribute.setPrefix(xmlStreamReader.getAttributePrefix(pIndex));
		attribute.setName(xmlStreamReader.getAttributeLocalName(pIndex));
		attribute.setValue(xmlStreamReader.getAttributeValue(pIndex));
		return attribute;
	}

	private Map<String, Object> listValuesToTreeValues(final Map<String, List<Value>> pMapValues) {
		final Map<String, Object> mapObjects = new OrderFiFoMap<String, Object>();
		for (final Map.Entry<String, List<Value>> entry : pMapValues.entrySet()) {
			if (entry.getKey().equals(
					"fr.cnamts.mk.metier.spfeltransport.to.sf.SPSoumisFacTranspSFTO/zFACTURE/zCOUVERTURE/fr.cnamts.mk.metier.spfeltransport.to.sf.CouvertureSFTO/zCONTRATS/fr.cnamts.mk.metier.spfeltransport.to.sf.ContratsAppliquesSFTO/zId")) {
			}
			if (entry.getValue().size() == 1) {
				mapObjects.put(entry.getKey(), entry.getValue().get(0).getValue());
				continue;
			}
			final Object object = listValuesToTreeValues(entry.getValue());
			int level = countLevel(entry.getValue());
			if (object.getClass().isArray()) {
				adaptTreeValues((Object[]) object, level);
			}
			mapObjects.put(entry.getKey(), object);
		}
		return mapObjects;
	}

	private Object listValuesToTreeValues(final List<Value> pValues) {
		if (pValues == null || pValues.isEmpty()) {
			return null;
		}
		if (pValues.size() == 1) {
			return pValues.get(0).getValue();
		}

		List<Object> listObjects = new ArrayList<Object>();

		final int level = pValues.get(0).getIndex().length;
		int iLevel;
		for (iLevel = 0; iLevel < level; iLevel++) {
			int lastIndex = -1;
			List<Value> levelValues = new ArrayList<Value>();
			for (int iValue = 0; iValue < pValues.size(); iValue++) {
				final Value value = pValues.get(iValue);
				if (levelValues.isEmpty()) {
					levelValues.add(value);
					lastIndex = value.getIndex()[iLevel];
					continue;
				}
				if (lastIndex == value.getIndex()[iLevel]) {
					levelValues.add(value);
					lastIndex = value.getIndex()[iLevel];
					continue;
				}
				lastIndex = value.getIndex()[iLevel];
				Object object = listValuesToTreeValues(levelValues);
				listObjects.add(object);
				levelValues = new ArrayList<Value>();
				levelValues.add(value);
			}
			if (listObjects.isEmpty()) {
				listObjects.clear();
				continue;
			}
			Object object = listValuesToTreeValues(levelValues);
			listObjects.add(object);
			break;
		}

		return listObjects.toArray();
	}

	private void adaptTreeValues(final Object[] pTreeValues, final int pLevel) {
		if (pLevel <= 1) {
			return;
		}
		for (int i = 0; i < pTreeValues.length; i++) {
			if (!pTreeValues[i].getClass().isArray()) {
				pTreeValues[i] = new Object[]{pTreeValues[i]};
			}
			adaptTreeValues((Object[]) pTreeValues[i], pLevel - 1);
		}
	}

	private int countLevel(final List<Value> pValues) {
		boolean[] model = createLevelModel(pValues);
		int level = 0;
		for (int i = 0; i < model.length; i++) {
			if (model[i]) {
				level++;
			}
		}
		return level;
	}

	private boolean[] createLevelModel(final List<Value> pValues) {
		final int[] index = pValues.get(0).getIndex();
		final boolean[] retval = new boolean[index.length];
		for (int iLevel = 0; iLevel < index.length; iLevel++) {
			int countUniq = 0;
			int lastIndex = -1;
			for (int iValue = 0; iValue < pValues.size(); iValue++) {
				if (lastIndex != pValues.get(iValue).getIndex()[iLevel]) {
					countUniq++;
				}
				lastIndex = pValues.get(iValue).getIndex()[iLevel];
			}
			retval[iLevel] = countUniq != 1;
		}
		return retval;
	}
}
