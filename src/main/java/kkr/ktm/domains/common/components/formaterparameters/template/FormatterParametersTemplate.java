package kkr.ktm.domains.common.components.formaterparameters.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.domains.common.components.formaterparameters.FormatterParameters;
import kkr.ktm.domains.common.components.formaterparameters.template.content.Block;
import kkr.ktm.domains.common.components.formaterparameters.template.content.Content;
import kkr.ktm.domains.common.components.formaterparameters.template.content.If;
import kkr.ktm.domains.common.components.formaterparameters.template.content.Loop;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.Close;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.Open;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.Part;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagEnd;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagIf;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagIndex;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagLoop;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagParameter;
import kkr.ktm.domains.common.components.formaterparameters.template.parts.Text;
import kkr.ktm.domains.common.components.formaterparameters.template.tags.Attribute;
import kkr.ktm.domains.common.components.formaterparameters.template.tags.Tag;
import kkr.ktm.exception.BaseException;

public class FormatterParametersTemplate extends FormatterParametersTemplateFwk implements FormatterParameters {
	private static final Logger LOG = Logger.getLogger(FormatterParametersTemplate.class);

	private static final String[] TAGS = new String[]{TagLoop.TAG, TagEnd.TAG, TagIf.TAG, TagParameter.TAG, TagIndex.TAG};

	private class Position {
		String source;
		int position = 0;

		public Position(String source) {
			this.source = source;
		}

		public String toString() {
			return (source != null ? "[" + source + "] " : "") + position;
		}
	}

	public String format(String pSource, final Map<String, Object> pParameters) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			final List<Object> contents = createTags(pSource);
			final List<Part> parts = createParts(contents);
			final Content content = createContent(parts, pParameters);
			final Content contentEvaluated = evaluateContent(content, pParameters, null);
			String retval = contentToString(contentEvaluated);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Content evaluateContent(Content pContentSource, Map<String, Object> pParameters, Map<String, Integer> indexes) throws BaseException {
		Content contentTarget = new Content();

		for (int i = 0; i < pContentSource.getContents().size(); i++) {
			Object object = pContentSource.getContents().get(i);
			if (object instanceof Text) {
				contentTarget.getContents().add(object);
			} else if (object instanceof TagParameter) {
				TagParameter tagParameter = (TagParameter) object;
				int[] tagIndexes = evaluateIndexes(indexes, tagParameter.getIndexes());

				String value = evaluateParameter(tagParameter.getName(), tagIndexes, pParameters);
				if (tagParameter.getFormat() != null) {
					try {
						String.format(tagParameter.getFormat(), value);
					} catch (Exception ex) {
						throw new TemplateConfigurationException(null,
								"Bad format string for a STRING value [" + tagParameter.getTagName() + "]: " + tagParameter.getFormat());
					}
				}
				Text text = new Text();
				text.setValue(value);
				contentTarget.getContents().add(text);
			} else if (object instanceof TagIndex) {
				TagIndex tagIndex = (TagIndex) object;
				Integer value = indexes.get(tagIndex.getName());
				if (value == null) {
					throw new TemplateConfigurationException(null,
							"Unknown index requested by the tag [" + tagIndex.getTagName() + "]: " + tagIndex.getName());
				}
				Text text = new Text();
				if (tagIndex.getFormat() != null) {
					try {
						text.setValue(String.format(tagIndex.getFormat(), value));
					} catch (Exception ex) {
						throw new TemplateConfigurationException(null,
								"Bad format string for a INTEGER value [" + tagIndex.getTagName() + "]: " + tagIndex.getName());
					}
				} else {
					text.setValue(String.valueOf(value));
				}
				contentTarget.getContents().add(text);
			} else if (object instanceof If) {
				If iff = (If) object;
				TagIf tagIf = iff.getTag();

				int[] tagIndexes = evaluateIndexes(indexes, tagIf.getIndexes());

				String valueParameter = evaluateParameter(tagIf.getName(), tagIndexes, pParameters);

				boolean evaluate = true;

				if (tagIf.getType() == TagIf.Type.EMPTY) {
					if (tagIf.getValid() != valueParameter.isEmpty()) {
						evaluate = false;
					}
				} else if (tagIf.getType() == TagIf.Type.NONEMPTY) {
					if (tagIf.getValid() != (!valueParameter.isEmpty())) {
						evaluate = false;
					}
				} else if (tagIf.getType() == TagIf.Type.NE) {
					if (tagIf.getValid() != (!valueParameter.equals(tagIf.getValue()))) {
						evaluate = false;
					}
				} else if (tagIf.getType() == TagIf.Type.EQ) {
					if (tagIf.getValid() != valueParameter.equals(tagIf.getValue())) {
						evaluate = false;
					}
				}

				if (evaluate) {
					Content content = evaluateContent(iff.getContent(), pParameters, indexes);
					contentTarget.getContents().addAll(content.getContents());
				}
			} else if (object instanceof Loop) {
				Loop loop = (Loop) object;
				TagLoop tagLoop = loop.getTag();

				int[] tagIndexes = evaluateIndexes(indexes, tagLoop.getIndexes());

				Integer count = 0;
				if (tagLoop.getType() == TagLoop.Type.COUNT) {
					String valueParameter = evaluateParameter(tagLoop.getName(), tagIndexes, pParameters);

					count = toInteger(valueParameter);
					if (count == null || count < 0) {
						throw new TemplateConfigurationException(null,
								"The value of the parameter " + tagLoop.getName() + toStringIndexes(tagIndexes) + " must be a non negativ integer");
					}
				} else if (tagLoop.getType() == TagLoop.Type.LENGTH) {
					Object objectParameter = pParameters.get(tagLoop.getName());
					if (objectParameter == null) {
						throw new TemplateConfigurationException(null, "Unknown parameter: " + tagLoop.getName());
					}
					Object objectLevel = retrieveObjectLevel(tagLoop.getName(), objectParameter, tagIndexes);
					count = evaluateListLength(objectLevel);
				}

				Map<String, Integer> indexesLoc = new LinkedHashMap<String, Integer>();

				if (indexes != null) {
					if (indexes.containsKey(tagLoop.getIndex())) {
						throw new TemplateConfigurationException(null, "The loop index " + tagLoop.getIndex() + " is already used by a parent loop");
					}
					indexesLoc.putAll(indexes);
				}
				for (int iCount = 1; iCount <= count; iCount++) {
					indexesLoc.put(tagLoop.getIndex(), iCount);
					Content content = evaluateContent(loop.getContent(), pParameters, indexesLoc);
					contentTarget.getContents().addAll(content.getContents());
				}
			} else {
				throw new TemplateConfigurationException(null, "Unknown content part: " + object.getClass().getSimpleName());
			}
		}

		return contentTarget;
	}

	private Object retrieveObjectLevel(String name, Object object, int[] indexes) throws BaseException {
		Object retval = null;
		Object objectCurrent = object;
		if (indexes != null && indexes.length > 0) {
			for (int index : indexes) {
				if (index > 1) {
					Object[] array;
					if (objectCurrent == null || !objectCurrent.getClass().isArray() || (array = (Object[]) objectCurrent).length < index) {
						throw new TemplateConfigurationException(null,
								"Not enough values of the parameter " + name + " for the index: " + toStringIndexes(indexes));
					}
					retval = objectCurrent = array[index - 1];
				} else {
					if (objectCurrent == null) {
						objectCurrent = "";
					}
					if (objectCurrent.getClass().isArray()) {
						Object[] array = (Object[]) objectCurrent;
						if (array.length < 1) {
							retval = objectCurrent = "";
						} else {
							retval = objectCurrent = array[0];
						}
					} else {
						retval = objectCurrent;
						objectCurrent = null;
					}
				}
			}
			if (retval == null) {
				throw new TemplateConfigurationException(null,
						"Not enough values of the parameter " + name + " for the index: " + toStringIndexes(indexes));
			}
		} else {
			return object;
		}
		return retval;
	}

	private Integer evaluateListLength(Object pObject) {
		return pObject == null
				? //
				0
				: pObject.getClass().isArray() ? //
						((Object[]) pObject).length : "".equals(pObject) ? //
								0 : 1;
	}

	private int[] evaluateIndexes(Map<String, Integer> indexes, String[] indexNames) throws BaseException {
		int[] values = new int[indexNames != null ? indexNames.length : 0];
		for (int i = 0; i < values.length; i++) {
			Integer value = indexes.get(indexNames[i]);
			if (value == null) {
				throw new TemplateConfigurationException(null, "Unknown index name: " + indexNames[i]);
			}
			values[i] = value;
		}
		return values;
	}

	private String toStringIndexes(int[] indexes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indexes.length; i++) {
			buffer.append("[").append(indexes[i]).append("]");
		}
		return buffer.toString();
	}

	private String evaluateParameter(String name, int[] indexes, final Map<String, Object> parameters) throws BaseException {
		if (!parameters.containsKey(name)) {
			throw new TemplateConfigurationException(null, "Unknown parameter: " + name);
		}

		Object object = parameters.get(name);

		Object objectLevel = retrieveObjectLevel(name, object, indexes);
		String value = evaluateValue(objectLevel);
		if (value == null) {
			throw new TemplateConfigurationException(null, "The parameter " + name + toStringIndexes(indexes) + " must contain a scalar");
		}
		return value;
	}

	private String evaluateValue(Object value) {
		if (value == null) {
			return "";
		}
		if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			if (array.length == 1) {
				return String.valueOf(array[0]);
			} else if (array.length == 0) {
				return "";
			} else {
				return null;
			}
		} else {
			return String.valueOf(value);
		}
	}

	private Content createContent(List<Part> pParts, Map<String, Object> pParameters) throws BaseException {
		Content contentRoot = new Content();
		Content contentCurrent = contentRoot;

		LinkedList<Content> contents = new LinkedList<Content>();

		for (int i = 0; i < pParts.size(); i++) {
			Part part = pParts.get(i);
			if (part instanceof Open) {
				Block block = createBlock((Open) part);
				if (block == null) {
					throw new TemplateConfigurationException(null, "Unknown block: " + ((Open) part).getTagName());
				}
				contentCurrent.getContents().add(block);
				contents.add(contentCurrent);
				contentCurrent = new Content();
				block.setContent(contentCurrent);
			} else if (part instanceof Close) {
				if (contents.isEmpty()) {
					throw new TemplateConfigurationException(null, "Closing tag without opening tag");
				}
				contentCurrent = contents.removeLast();
			} else {
				contentCurrent.getContents().add(part);
			}
		}

		if (!contents.isEmpty()) {
			throw new TemplateConfigurationException(null, "Opening tag without closing tag");
		}

		return contentRoot;
	}

	private Block createBlock(Open pOpen) {
		if (pOpen instanceof TagIf) {
			final If blockIf = new If();
			blockIf.setTag((TagIf) pOpen);
			return blockIf;
		} else if (pOpen instanceof TagLoop) {
			final Loop blockLoop = new Loop();
			blockLoop.setTag((TagLoop) pOpen);
			return blockLoop;
		}
		return null;
	}

	private List<Part> createParts(List<Object> pContents) throws BaseException {
		List<Part> parts = new ArrayList<Part>();
		for (Object object : pContents) {
			if (object instanceof String) {
				final Text text = new Text();
				text.setValue((String) object);
				parts.add(text);
			} else if (object instanceof Tag) {
				final Part part = createPart((Tag) object);
				parts.add(part);
			}
		}
		return parts;
	}

	private Part createPart(Tag pTag) throws BaseException {
		if (TagParameter.TAG.equals(pTag.getName())) {
			final TagParameter tagParameter = new TagParameter();
			for (Map.Entry<String, String> entry : pTag.getAttributes().entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();
				if (TagParameter.ATTR_NAME.equals(attributeName)) {
					if (!isNameParameter(attributeValue)) {
						throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute [" + pTag.getName() + " "
								+ TagParameter.ATTR_NAME + "]" + TagParameter.ATTR_NAME + " as a parameter name: " + attributeValue);
					}
					tagParameter.setName(attributeValue);
				} else if (TagParameter.ATTR_INDEXES.equals(attributeName)) {
					String[] indexes = toArray(attributeValue);
					if (indexes == null) {
						throw new TemplateConfigurationException(null,
								"Cannot evaluate the value of the attribute [" + pTag.getName() + " " + TagParameter.ATTR_INDEXES + "]"
										+ TagParameter.ATTR_NAME + "  as a comma separated list of index names: " + attributeValue);
					}
					for (String index : indexes) {
						if (!isNameIndex(index)) {
							throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute [" + pTag.getName() + " "
									+ TagParameter.ATTR_INDEXES + "]" + " an index name has bad format: " + attributeValue);
						}
					}
					tagParameter.setIndexes(indexes);
				} else if (TagParameter.ATTR_FORMAT.equals(attributeName)) {
					try {
						String.format(attributeValue, "");
					} catch (Exception ex) {
						throw new TemplateConfigurationException(null, "Bad format of the attribute [" + pTag.getName() + " " + TagIndex.ATTR_FORMAT
								+ "]" + " value: " + attributeValue + " problem: " + ex.getMessage());
					}
					tagParameter.setFormat(attributeValue);
				} else {
					throw new TemplateConfigurationException(null, "Unknown attribute '" + attributeName + "' for the tag '" + pTag.getName() + "'");
				}
			}
			if (tagParameter.getName() == null) {
				throw new TemplateConfigurationException(null,
						"Attribute " + TagParameter.ATTR_NAME + " is required for the tag " + TagParameter.TAG);
			}
			return tagParameter;

		} else if (TagIndex.TAG.equals(pTag.getName())) {
			final TagIndex tagIndex = new TagIndex();
			for (Map.Entry<String, String> entry : pTag.getAttributes().entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();
				if (TagIndex.ATTR_NAME.equals(attributeName)) {
					if (!isNameIndex(attributeValue)) {
						throw new TemplateConfigurationException(null,
								"Cannot evaluate the value of the attribute '" + TagIndex.ATTR_NAME + "' as a index name: " + pTag.getName());
					}
					tagIndex.setName(attributeValue);
				} else if (TagIndex.ATTR_FORMAT.equals(attributeName)) {
					try {
						String.format(attributeValue, 0);
					} catch (Exception ex) {
						throw new TemplateConfigurationException(null, "Bad format of the attribute [" + pTag.getName() + " " + TagIndex.ATTR_FORMAT
								+ "]" + " value: " + attributeValue + " problem: " + ex.getMessage());
					}
					tagIndex.setFormat(attributeValue);
				} else {
					throw new TemplateConfigurationException(null, "Unknown attribute '" + attributeName + "' for the tag '" + pTag.getName() + "'");
				}
			}
			return tagIndex;
		} else if (TagLoop.TAG.equals(pTag.getName())) {
			final TagLoop tagLoop = new TagLoop();
			for (Map.Entry<String, String> entry : pTag.getAttributes().entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();
				if (TagLoop.ATTR_INDEX.equals(attributeName)) {
					if (!isNameParameter(attributeValue)) {
						throw new TemplateConfigurationException(null,
								"Cannot evaluate the value of the attribute '" + TagLoop.ATTR_INDEX + "' as a parameter name: " + pTag.getName());
					}
					tagLoop.setIndex(attributeValue);
				} else if (TagLoop.ATTR_NAME.equals(attributeName)) {
					if (!isNameParameter(attributeValue)) {
						throw new TemplateConfigurationException(null,
								"Cannot evaluate the attribute [" + pTag.getName() + " " + TagLoop.ATTR_NAME + "] as a parameter name");
					}
					tagLoop.setName(attributeValue);
				} else if (TagLoop.ATTR_INDEXES.equals(attributeName)) {
					String[] indexes = toArray(attributeValue);
					if (indexes == null) {
						throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute '" + TagParameter.ATTR_INDEXES
								+ "' as a comma separated list of index names: " + pTag.getName());
					}
					tagLoop.setIndexes(indexes);
				} else if (TagLoop.ATTR_TYPE.equals(attributeName)) {
					if (TagLoop.TYPE_COUNT.equals(attributeValue)) {
						tagLoop.setType(TagLoop.Type.COUNT);
					} else if (TagLoop.TYPE_LENGTH.equals(attributeValue)) {
						tagLoop.setType(TagLoop.Type.LENGTH);
					} else {
						throw new TemplateConfigurationException(null, "The value of the attribute [" + pTag.getName() + " " + TagLoop.ATTR_TYPE
								+ "] must be " + TagLoop.Type.COUNT + " or " + TagLoop.Type.LENGTH);
					}
				} else {
					throw new TemplateConfigurationException(null, "Unknown attribute '" + attributeName + "' for the tag '" + TagLoop.TAG + "'");
				}
			}
			if (tagLoop.getIndex() == null) {
				throw new TemplateConfigurationException(null, "attribute '" + TagLoop.ATTR_INDEX + "' is required for the tag " + TagLoop.TAG);
			}
			if (tagLoop.getIndexes() == null) {
				tagLoop.setIndexes(new String[0]);;
			}
			if (tagLoop.getName() == null) {
				throw new TemplateConfigurationException(null, "Attribute [" + TagLoop.TAG + " " + TagLoop.ATTR_NAME + "] must be defined.");
			}
			if (tagLoop.getType() == null) {
				throw new TemplateConfigurationException(null, "Attribute [" + TagLoop.TAG + " " + TagLoop.ATTR_NAME + "] must be defined.");
			}
			return tagLoop;
		} else if (TagIf.TAG.equals(pTag.getName())) {
			final TagIf tagIf = new TagIf();
			for (Map.Entry<String, String> entry : pTag.getAttributes().entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();
				if (TagIf.ATTR_VALID.equals(attributeName)) {
					if ("TRUE".equals(attributeValue)) {
						tagIf.setValid(true);
					} else if ("FALSE".equals(attributeValue)) {
						tagIf.setValid(false);
					} else {
						throw new TemplateConfigurationException(null,
								"The value of the attribute [" + pTag.getName() + " " + TagIf.ATTR_VALID + "] must be TRUE or FALSE");
					}
				} else if (TagIf.ATTR_NAME.equals(attributeName)) {
					if (!isNameParameter(attributeValue)) {
						throw new TemplateConfigurationException(null,
								"Cannot evaluate the attribute [" + pTag.getName() + " " + TagIf.ATTR_NAME + "] as a parameter name");
					}
					tagIf.setName(attributeValue);
				} else if (TagIf.ATTR_VALUE.equals(attributeName)) {
					tagIf.setValue(attributeValue);
				} else if (TagIf.ATTR_INDEXES.equals(attributeName)) {
					String[] indexes = toArray(attributeValue);
					if (indexes == null) {
						throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute '" + TagParameter.ATTR_INDEXES
								+ "' as a comma separated list of index names: " + pTag.getName());
					}
					tagIf.setIndexes(indexes);
				} else if (TagIf.ATTR_TYPE.equals(attributeName)) {
					if (TagIf.TYPE_EMPTY.equals(attributeValue)) {
						tagIf.setType(TagIf.Type.EMPTY);
					} else if (TagIf.TYPE_NONEMPTY.equals(attributeValue)) {
						tagIf.setType(TagIf.Type.NONEMPTY);
					} else if (TagIf.TYPE_EQ.equals(attributeValue)) {
						tagIf.setType(TagIf.Type.EQ);
					} else if (TagIf.TYPE_NE.equals(attributeValue)) {
						tagIf.setType(TagIf.Type.NE);
					} else {
						throw new TemplateConfigurationException(null, "The value of the attribute [" + pTag.getName() + " " + TagIf.ATTR_TYPE
								+ "] must be " + TagIf.Type.EMPTY + "," + TagIf.Type.NONEMPTY + "," + TagIf.Type.NE + "," + TagIf.Type.EQ);
					}
				} else {
					throw new TemplateConfigurationException(null, "Unknown attribute '" + attributeName + "' for the tag '" + TagIf.TAG + "'");
				}
			}

			if (tagIf.getValid() == null) {
				tagIf.setValid(true);
			}
			if (tagIf.getIndexes() == null) {
				tagIf.setIndexes(new String[0]);;
			}
			if (tagIf.getName() == null) {
				throw new TemplateConfigurationException(null, "Attribute [" + TagIf.TAG + " " + TagIf.ATTR_NAME + "] must be defined.");
			}
			if (tagIf.getType() == null) {
				throw new TemplateConfigurationException(null, "Attribute [" + TagIf.TAG + " " + TagIf.ATTR_NAME + "] must be defined.");
			} else if ((tagIf.getType() == TagIf.Type.EQ || tagIf.getType() == TagIf.Type.NE) && tagIf.getValue() == null) {
				throw new TemplateConfigurationException(null, "The attribut " + TagIf.ATTR_VALUE + " is expected when the value of the attribute ["
						+ pTag.getName() + " " + TagIf.ATTR_TYPE + "=\"" + tagIf.getType() + "\"]");
			}
			return tagIf;
		} else if (TagEnd.TAG.equals(pTag.getName())) {
			if (!pTag.getAttributes().isEmpty()) {
				throw new TemplateConfigurationException(null,
						"Unknown attribute '" + pTag.getAttributes().get(0) + "' for the tag '" + TagEnd.TAG + "'");
			}
			return new TagEnd();
		} else {
			throw new TemplateConfigurationException(null, "Unknown tag '" + pTag.getName() + "'");
		}
	}

	private Integer toInteger(String pText) {
		try {
			return Integer.parseInt(pText);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private String[] toArray(String value) {
		String[] array = value.split(",");
		if (array.length == 0) {
			return null;
		}
		for (int i = 0; i < array.length; i++) {
			String adapt = array[i].trim();
			if (adapt.length() == 0) {
				return null;
			} else {
				array[i] = adapt;
			}
		}
		return array;
	}

	private boolean isNameParameter(String pText) {
		if (pText.isEmpty()) {
			return false;
		}
		for (int i = 0; i < pText.length(); i++) {
			char c = pText.charAt(i);
			if (!Character.isJavaIdentifierPart(c) && c != '-' && c != '.' && c != '/') {
				return false;
			}
		}
		return true;
	}

	private boolean isNameIndex(String pText) {
		if (pText.isEmpty()) {
			return false;
		}
		for (int i = 0; i < pText.length(); i++) {
			char c = pText.charAt(i);
			if (!Character.isJavaIdentifierPart(c)) {
				return false;
			}
		}
		return true;
	}

	private String contentToString(Content pContent) throws BaseException {
		final StringBuilder builder = new StringBuilder();
		for (Object object : pContent.getContents()) {
			if (object instanceof Text) {
				builder.append(((Text) object).getValue());
			} else if (object instanceof Content) {
				final String contentLoop = contentToString((Content) object);
				builder.append(contentLoop);
			}
		}
		return builder.toString();
	}

	private List<Object> createTags(String source) throws BaseException {
		final List<Object> contents = new ArrayList<Object>();

		final StringBuilder builder = new StringBuilder(source);
		final Position p = new Position(source);

		while (true) {
			final int current = p.position;
			final int indexTag = nextTag(builder, p.position);
			if (indexTag != -1) {
				final String text = builder.substring(current, indexTag);
				contents.add(text);
				p.position = indexTag;
				final Tag tag = createTag(builder, p);
				contents.add(tag);
			} else {
				final String text = builder.substring(current);
				contents.add(text);
				break;
			}
		}

		return contents;
	}

	private Tag createTag(StringBuilder pBuilder, final Position pP) throws BaseException {
		Tag tag = new Tag();

		//
		// [
		//
		pP.position++;
		pP.position += countSpace(pBuilder, pP.position);

		//
		// Name
		//
		int diff = countName(pBuilder, pP.position);
		final String name = pBuilder.substring(pP.position, pP.position + diff);
		tag.setName(name);
		pP.position += diff;
		diff = countSpace(pBuilder, pP.position);

		if (!isBorder(pBuilder, pP.position)) {
			throw new TemplateConfigurationException(null, "Unexpected character: " + pP);
		}

		//
		// End
		//
		if (pBuilder.charAt(pP.position + diff) == ']') {
			pP.position += diff;
			pP.position++;
			return tag;
		}

		pP.position += diff;

		//
		// Attribute
		//
		while (true) {
			final Attribute attribute = createAttribute(pBuilder, pP);
			if (tag.getAttributes().containsKey(attribute.getName())) {
				throw new TemplateConfigurationException(null, "Attribute " + attribute.getName() + " exists already in the tag " + tag.getName());
			}
			tag.getAttributes().put(attribute.getName(), attribute.getValue());

			if (!isBorder(pBuilder, pP.position)) {
				throw new TemplateConfigurationException(null, "Unexpected character on position: " + pP);
			}

			diff = countSpace(pBuilder, pP.position);
			pP.position += diff;

			//
			// End
			//
			if (pBuilder.charAt(pP.position) == ']') {
				pP.position += diff;
				pP.position++;
				return tag;
			}

			if (pP.position >= pBuilder.length()) {
				throw new TemplateConfigurationException(null, "Missing the character '[': " + pP);
			}
		}
	}

	private Attribute createAttribute(StringBuilder pBuilder, final Position pP) throws BaseException {
		final Attribute attribute = new Attribute();

		int diff = countSpace(pBuilder, pP.position);
		pP.position += diff;

		//
		// Name
		//
		diff = countName(pBuilder, pP.position);
		if (diff == 0) {
			throw new TemplateConfigurationException(null, "Cannot extract attribute name: " + pP);
		}
		final String name = pBuilder.substring(pP.position, pP.position + diff);
		attribute.setName(name);
		pP.position += diff;
		diff = countSpace(pBuilder, pP.position);
		pP.position += diff;

		//
		// =
		//
		if (pBuilder.charAt(pP.position) != '=') {
			throw new TemplateConfigurationException(null, "Attribut is missing '=': " + pP);
		}
		pP.position++;
		diff = countSpace(pBuilder, pP.position);
		pP.position += diff;

		//
		// Value
		//
		final boolean quot = pBuilder.charAt(pP.position) == '"';
		if (quot) {
			pP.position++;
			final int quotPos = pBuilder.indexOf("\"", pP.position);
			if (quotPos == -1) {
				throw new TemplateConfigurationException(null, "Quotations are not closed: " + pP);
			}
			final String value = pBuilder.substring(pP.position, quotPos);
			attribute.setValue(value);
			pP.position = quotPos + 1;
		} else {
			diff = countValue(pBuilder, pP.position);
			final String value = pBuilder.substring(pP.position, pP.position + diff);
			pP.position += diff;
			attribute.setValue(value);
			if (!isBorder(pBuilder, pP.position)) {
				throw new TemplateConfigurationException(null, "Unexpected character: " + pP);
			}
		}

		return attribute;
	}

	private int countValue(StringBuilder pBuilder, final int pPos) {
		int j = 0;
		for (int i = pPos; i < pBuilder.length(); i++, j++) {
			char c = pBuilder.charAt(i);
			if (!Character.isJavaIdentifierPart(c) && c != '-') {
				break;
			}
		}
		return j;
	}

	private int countName(StringBuilder pBuilder, final int pPos) {
		int j = 0;
		for (int i = pPos; i < pBuilder.length(); i++, j++) {
			char c = pBuilder.charAt(i);
			if (j == 0 && !Character.isJavaIdentifierStart(c) || !Character.isJavaIdentifierPart(c) && c != '-' && c != '.') {
				break;
			}
		}
		return j;
	}

	private int countSpace(StringBuilder pBuilder, final int pPos) {
		int j = 0;
		for (int i = pPos; i < pBuilder.length() && Character.isWhitespace(pBuilder.charAt(i)); i++, j++) {
		}
		return j;
	}

	private int nextTag(StringBuilder pBuilder, final int pPos) {

		int min = -1;

		for (String tag : TAGS) {
			int found = pBuilder.indexOf(tag, pPos);
			if (found == -1) {
				continue;
			}
			int startTag = startTag(pBuilder, found, tag);
			if (startTag == -1) {
				continue;
			}
			if (min == -1 || startTag < min) {
				min = startTag;
			}
		}

		return min;
	}

	private int startTag(StringBuilder pBuilder, final int pPos, final String pTag) {
		int startTag = -1;
		for (int i = pPos - 1; i >= 0; i--) {
			char c = pBuilder.charAt(i);
			if (c == '[') {
				startTag = i;
				break;
			}
			if (Character.isWhitespace(c)) {
				continue;
			}
			return -1;
		}
		char lastChar = pBuilder.charAt(pPos + pTag.length());
		if (Character.isWhitespace(lastChar) || //
				lastChar == ']') {
			return startTag;
		}
		return -1;
	}

	private boolean isBorder(StringBuilder pBuilder, final int pPos) {
		final char c = pBuilder.charAt(pPos);
		return Character.isWhitespace(c) || c == ']';
	}
}
