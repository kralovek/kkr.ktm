package kkr.ktm.domains.common.components.parametersformater.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.utils.UtilsString;
import kkr.ktm.domains.common.components.expressionparser.Expression;
import kkr.ktm.domains.common.components.parametersformater.ParametersFormatter;
import kkr.ktm.domains.common.components.parametersformater.template.content.Block;
import kkr.ktm.domains.common.components.parametersformater.template.content.Content;
import kkr.ktm.domains.common.components.parametersformater.template.content.If;
import kkr.ktm.domains.common.components.parametersformater.template.content.Loop;
import kkr.ktm.domains.common.components.parametersformater.template.format.Format;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatBase;
import kkr.ktm.domains.common.components.parametersformater.template.format.FormatType;
import kkr.ktm.domains.common.components.parametersformater.template.parts.Close;
import kkr.ktm.domains.common.components.parametersformater.template.parts.Open;
import kkr.ktm.domains.common.components.parametersformater.template.parts.Part;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagEnd;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagIf;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagIndex;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagLoop;
import kkr.ktm.domains.common.components.parametersformater.template.parts.TagParameter;
import kkr.ktm.domains.common.components.parametersformater.template.parts.Text;
import kkr.ktm.domains.common.components.parametersformater.template.tags.Attribute;
import kkr.ktm.domains.common.components.parametersformater.template.tags.Tag;
import kkr.ktm.domains.common.components.parametersformater.template.value.Value;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueBase;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueDecimal;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueInteger;
import kkr.ktm.domains.common.components.parametersformater.template.value.ValueText;

public class ParametersFormatterTemplate extends ParametersFormatterTemplateFwk implements ParametersFormatter {
	private static final Logger LOG = Logger.getLogger(ParametersFormatterTemplate.class);

	private static final String[] TAGS = new String[] { TagLoop.TAG, TagEnd.TAG, TagIf.TAG, TagParameter.TAG,
			TagIndex.TAG };

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

	public String format(String source, Map<String, Object> parameters) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			List<Object> contents = createTags(source);
			List<Part> parts = createParts(contents);
			Content content = createContent(parts, parameters);
			Content contentEvaluated = evaluateContent(content, parameters, null);
			String retval = contentToString(contentEvaluated);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private Map<String, Number> extractNumericParameters(Map<String, Object> parameters) {
		Map<String, Number> retval = new HashMap<String, Number>();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			Object object = entry.getValue();

			for (; true //
					&& object != null //
					&& object.getClass().isArray() //
					&& ((Object[]) object).length == 1; //
					object = ((Object[]) object)[0]) {
				// nothing to do
			}

			if (object == null) {
				continue;
			}

			if (object instanceof Number) {
				retval.put(entry.getKey(), (Number) object);
				continue;
			}
			if (object instanceof String) {
				try {
					Double value = Double.parseDouble((String) object);
					retval.put(entry.getKey(), value);
				} catch (NumberFormatException ex) {
					// OK
				}
			}
		}
		return retval;
	}

	private void evaluateTagParameter(Content contentTarget, TagParameter tagParameter, Map<String, Object> parameters,
			Map<String, Integer> indexes) throws BaseException {
		int[] tagIndexes = evaluateIndexes(indexes, tagParameter.getIndexes());

		Value value = evaluateParameter(tagParameter.getName(), tagIndexes, parameters);
		try {
			String formatedValue = tagParameter.getFormat().format(value);
			Text text = new Text();
			text.setValue(formatedValue);
			contentTarget.getContents().add(text);
		} catch (Exception ex) {
			throw new TemplateConfigurationException(null, "Bad format string for a STRING value ["
					+ tagParameter.getTagName() + "]: " + tagParameter.getFormat(), ex);
		}
	}

	private void evaluateTagIndex(Content contentTarget, TagIndex tagIndex, Map<String, Object> parameters,
			Map<String, Integer> indexes, ContextIndexExpression context) throws BaseException {
		Integer value = indexes.get(tagIndex.getName());
		if (value == null) {
			throw new TemplateConfigurationException(null,
					"Unknown index requested by the tag [" + tagIndex.getTagName() + "]: " + tagIndex.getName());
		}
		try {
			Value formatValue;
			if (tagIndex.getExpression() != null) {
				context.setValueIndex(value);
				Number expressionValue = tagIndex.getExpression().evaluate(context);
				formatValue = new ValueInteger(expressionValue.longValue());
			} else {
				formatValue = new ValueInteger(value);
			}

			String formatedValue = tagIndex.getFormat().format(formatValue);
			Text text = new Text();
			text.setValue(formatedValue);
			contentTarget.getContents().add(text);
		} catch (Exception ex) {
			throw new TemplateConfigurationException(null,
					"Bad format string for a INTEGER value [" + tagIndex.getTagName() + "]: " + tagIndex.getName());
		}
	}

	private void evaluateTagIf(Content contentTarget, If iff, Map<String, Object> parameters,
			Map<String, Integer> indexes) throws BaseException {
		TagIf tagIf = iff.getTag();

		int[] tagIndexes = evaluateIndexes(indexes, tagIf.getIndexes());

		Value value = evaluateParameter(tagIf.getName(), tagIndexes, parameters);

		boolean evaluate = true;

		switch (tagIf.getType()) {
		case EMPTY:
			evaluate = value.isEmpty();
			break;
		case NONEMPTY:
			evaluate = !value.isEmpty();
			break;
		case EQ:
			evaluate = value.equals(tagIf.getValue());
			break;
		case NE:
			evaluate = !value.equals(tagIf.getValue());
			break;
		}

		if (evaluate) {
			Content content = evaluateContent(iff.getContent(), parameters, indexes);
			contentTarget.getContents().addAll(content.getContents());
		}
	}

	private void evaluateTagLoop(Content contentTarget, Loop loop, Map<String, Object> parameters,
			Map<String, Integer> indexes) throws BaseException {
		TagLoop tagLoop = loop.getTag();

		int[] tagIndexes = evaluateIndexes(indexes, tagLoop.getIndexes());

		Integer count = null;
		if (tagLoop.getType() == TagLoop.Type.COUNT) {
			Value valueParameter = evaluateParameter(tagLoop.getName(), tagIndexes, parameters);
			count = toInteger(valueParameter);
			if (count == null || count < 0) {
				throw new TemplateConfigurationException(null, "The value of the parameter " + tagLoop.getName()
						+ toStringIndexes(tagIndexes) + " must be a non negativ integer");
			}
		} else if (tagLoop.getType() == TagLoop.Type.LENGTH) {
			Object objectParameter = parameters.get(tagLoop.getName());
			if (objectParameter == null) {
				throw new TemplateConfigurationException(null, "Unknown parameter: " + tagLoop.getName());
			}
			Object objectLevel = retrieveObjectLevel(tagLoop.getName(), objectParameter, tagIndexes);
			count = evaluateListLength(objectLevel);
		} else {
			throw new TemplateConfigurationException(null, "Unsupported LOOP type: " + tagLoop.getType());
		}

		Map<String, Integer> indexesLoc = new LinkedHashMap<String, Integer>();

		if (indexes != null) {
			if (indexes.containsKey(tagLoop.getIndex())) {
				throw new TemplateConfigurationException(null,
						"The loop index " + tagLoop.getIndex() + " is already used by a parent loop");
			}
			indexesLoc.putAll(indexes);
		}
		// INDEXES
		// for (int iCount = 1; iCount <= count; iCount++) {
		for (int iCount = 0; iCount < count; iCount++) {
			indexesLoc.put(tagLoop.getIndex(), iCount);
			Content content = evaluateContent(loop.getContent(), parameters, indexesLoc);
			contentTarget.getContents().addAll(content.getContents());
		}
	}

	private Content evaluateContent(Content contentSource, Map<String, Object> parameters, Map<String, Integer> indexes)
			throws BaseException {
		Content contentTarget = new Content();

		Map<String, Number> numericParameters = extractNumericParameters(parameters);
		ContextIndexExpression context = new ContextIndexExpression(numericParameters);

		for (int i = 0; i < contentSource.getContents().size(); i++) {
			Object object = contentSource.getContents().get(i);
			if (object instanceof Text) {
				contentTarget.getContents().add(object);
			} else if (object instanceof TagParameter) {
				evaluateTagParameter(contentTarget, (TagParameter) object, parameters, indexes);
			} else if (object instanceof TagIndex) {
				evaluateTagIndex(contentTarget, (TagIndex) object, parameters, indexes, context);
			} else if (object instanceof If) {
				evaluateTagIf(contentTarget, (If) object, parameters, indexes);
			} else if (object instanceof Loop) {
				evaluateTagLoop(contentTarget, (Loop) object, parameters, indexes);
			} else {
				throw new TemplateConfigurationException(null,
						"Unknown content part: " + object.getClass().getSimpleName());
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
					if (objectCurrent == null || !objectCurrent.getClass().isArray()
							|| (array = (Object[]) objectCurrent).length < index) {
						throw new TemplateConfigurationException(null, "Not enough values of the parameter " + name
								+ " for the index: " + toStringIndexes(indexes));
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
		return pObject == null ? //
				0 : pObject.getClass().isArray() ? //
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

	private Value evaluateParameter(String name, int[] indexes, final Map<String, Object> parameters)
			throws BaseException {
		if (!parameters.containsKey(name)) {
			throw new TemplateConfigurationException(null, "Unknown parameter: " + name);
		}

		Object object = parameters.get(name);

		Object objectLevel = retrieveObjectLevel(name, object, indexes);

		try {
			Value value = ValueBase.newValue(objectLevel);
			return value;
		} catch (IllegalArgumentException ex) {
			throw new TemplateConfigurationException(null,
					"The parameter " + name + toStringIndexes(indexes) + " must contain a scalar of allowed type", ex);
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
				final Part part = createTag((Tag) object);
				parts.add(part);
			}
		}
		return parts;
	}

	private TagParameter createTagParameter(Tag tag) throws BaseException {
		Map<String, String> attributes = new HashMap<String, String>(tag.getAttributes());
		TagParameter tagParameter = new TagParameter();

		if (attributes.containsKey(TagParameter.ATTR_NAME)) {
			String attributeValue = attributes.remove(TagParameter.ATTR_NAME);
			if (!isNameParameter(attributeValue)) {
				throw new TemplateConfigurationException(null,
						"Cannot evaluate the value of the attribute [" + tag.getName() + " " + TagParameter.ATTR_NAME
								+ "] " + TagParameter.ATTR_NAME + " as a parameter name: " + attributeValue);
			}
			tagParameter.setName(attributeValue);
		}
		if (attributes.containsKey(TagParameter.ATTR_INDEXES)) {
			String attributeValue = attributes.remove(TagParameter.ATTR_INDEXES);
			String[] indexes = toArray(attributeValue);
			if (indexes == null) {
				throw new TemplateConfigurationException(null,
						"Cannot evaluate the value of the attribute [" + tag.getName() + " " + TagParameter.ATTR_INDEXES
								+ "]" + TagParameter.ATTR_NAME + "  as a comma separated list of index names: "
								+ attributeValue);
			}
			for (String index : indexes) {
				if (!isNameIndex(index)) {
					throw new TemplateConfigurationException(null,
							"Cannot evaluate the value of the attribute [" + tag.getName() + " "
									+ TagParameter.ATTR_INDEXES + "]" + " an index name has bad format: "
									+ attributeValue);
				}
			}
			tagParameter.setIndexes(indexes);
		}

		FormatType formatType = null;
		if (attributes.containsKey(TagParameter.ATTR_FORMAT_TYPE)) {
			String attributeValue = attributes.remove(TagParameter.ATTR_FORMAT_TYPE);
			try {
				formatType = FormatType.valueOf(attributeValue);
			} catch (Exception ex) {
				throw new TemplateConfigurationException(null,
						"Bad format-type of the attribute [" + tag.getName() + " " + TagParameter.ATTR_FORMAT_TYPE + "]"
								+ " value: " + attributeValue + " problem: " + ex.getMessage());
			}
		}

		{
			String attributeValue = attributes.remove(TagParameter.ATTR_FORMAT);
			if (formatType == null && !UtilsString.isEmpty(attributeValue)) {
				formatType = FormatType.VALUE;
			} else {
				formatType = FormatType.AUTO;
			}
			try {
				Format format = FormatBase.newFormat(formatType, attributeValue);
				tagParameter.setFormat(format);
			} catch (Exception ex) {
				throw new TemplateConfigurationException(null,
						"Bad format of the attribute [" + tag.getName() + " " + TagParameter.ATTR_FORMAT + "=\""
								+ attributeValue + "\"]" + " value: " + attributeValue + " problem: "
								+ ex.getMessage());
			}
		}

		if (!attributes.isEmpty()) {
			throw new TemplateConfigurationException(null, "Attribute " + attributes.keySet().iterator().next()
					+ " is required for the tag " + TagParameter.TAG);
		}
		return tagParameter;
	}

	private TagIndex createTagIndex(Tag tag) throws BaseException {
		Map<String, String> attributes = new HashMap<String, String>(tag.getAttributes());
		TagIndex tagIndex = new TagIndex();

		if (attributes.containsKey(TagIndex.ATTR_NAME)) {
			String attributeValue = attributes.remove(TagIndex.ATTR_NAME);
			if (!isNameIndex(attributeValue)) {
				throw new TemplateConfigurationException(null, "" //
						+ "Bad value of the attribute '" //
						+ "[" + tag.getName() + " " + TagIndex.ATTR_NAME + "=" + "\"" + attributeValue + "\"]" //
						+ ". The value is not as an index name");
			}
			tagIndex.setName(attributeValue);
		}
		if (attributes.containsKey(TagIndex.ATTR_FORMAT)) {
			String attributeValue = attributes.remove(TagIndex.ATTR_FORMAT);
			try {
				Format format = FormatBase.newFormat(FormatType.VALUE, attributeValue);
				format.format(new ValueInteger(0));
				tagIndex.setFormat(format);
			} catch (Exception ex) {
				throw new TemplateConfigurationException(null, "" //
						+ "Bad format of the attribute " //
						+ "[" + tag.getName() + " " + TagIndex.ATTR_FORMAT + "=" + "\"" + attributeValue + "\"]" //
						+ ". Problem: " + ex.getMessage());
			}
		} else {
			Format format = FormatBase.newFormat(FormatType.AUTO, null);
			tagIndex.setFormat(format);
		}

		if (attributes.containsKey(TagIndex.ATTR_EXPRESSION)) {
			String attributeValue = attributes.remove(TagIndex.ATTR_EXPRESSION);

			if (expressionParser == null) {
				throw new TemplateConfigurationException(null, "" //
						+ "Expression parser must be configured " //
						+ "[" + tag.getName() + " " + TagIndex.ATTR_EXPRESSION + "=" + "\"" + attributeValue + "\"]");
			}

			Expression expression = expressionParser.parseExpression(attributeValue);
			tagIndex.setExpression(expression);
		}

		if (!attributes.isEmpty()) {
			String attributeName = attributes.keySet().iterator().next();
			String attributeValue = attributes.remove(TagIndex.ATTR_FORMAT);

			throw new TemplateConfigurationException(null, "" //
					+ "Unknown attribute " //
					+ "[" + tag.getName() + " " + attributeName + "=" + "\"" + attributeValue + "\"]" //
					+ ".");
		}

		return tagIndex;
	}

	private TagLoop createTagLoop(Tag tag) throws BaseException {
		Map<String, String> attributes = new HashMap<String, String>(tag.getAttributes());
		TagLoop tagLoop = new TagLoop();

		if (attributes.containsKey(TagLoop.ATTR_INDEX)) {
			String attributeValue = attributes.remove(TagLoop.ATTR_INDEX);
			if (!isNameParameter(attributeValue)) {
				throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute '"
						+ TagLoop.ATTR_INDEX + "' as a parameter name: " + tag.getName());
			}
			tagLoop.setIndex(attributeValue);
		}
		if (attributes.containsKey(TagLoop.ATTR_NAME)) {
			String attributeValue = attributes.remove(TagLoop.ATTR_NAME);
			if (!isNameParameter(attributeValue)) {
				throw new TemplateConfigurationException(null, "Cannot evaluate the attribute [" + tag.getName() + " "
						+ TagLoop.ATTR_NAME + "] as a parameter name");
			}
			tagLoop.setName(attributeValue);
		}
		if (attributes.containsKey(TagLoop.ATTR_INDEXES)) {
			String attributeValue = attributes.remove(TagLoop.ATTR_INDEXES);
			String[] indexes = toArray(attributeValue);
			if (indexes == null) {
				throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute '"
						+ TagParameter.ATTR_INDEXES + "' as a comma separated list of index names: " + tag.getName());
			}
			tagLoop.setIndexes(indexes);
		}
		if (attributes.containsKey(TagLoop.ATTR_TYPE)) {
			String attributeValue = attributes.remove(TagLoop.ATTR_TYPE);
			TagLoop.Type type;
			try {
				type = TagLoop.Type.valueOf(attributeValue);
				tagLoop.setType(type);
			} catch (Exception ex) {
				throw new TemplateConfigurationException(null, "Bad value of the attribute [" + tag.getName() + " "
						+ TagLoop.ATTR_TYPE + "]" + " value: " + attributeValue + " problem: " + ex.getMessage());
			}
		}

		if (!attributes.isEmpty()) {
			throw new TemplateConfigurationException(null,
					"Unknown attribute " + attributes.keySet().iterator().next() + " in the tag " + TagLoop.TAG);
		}

		if (tagLoop.getIndex() == null) {
			throw new TemplateConfigurationException(null,
					"attribute '" + TagLoop.ATTR_INDEX + "' is required for the tag " + TagLoop.TAG);
		}
		if (tagLoop.getIndexes() == null) {
			tagLoop.setIndexes(new String[0]);
		}
		if (tagLoop.getName() == null) {
			throw new TemplateConfigurationException(null,
					"Attribute [" + TagLoop.TAG + " " + TagLoop.ATTR_NAME + "] must be defined.");
		}
		if (tagLoop.getType() == null) {
			throw new TemplateConfigurationException(null,
					"Attribute [" + TagLoop.TAG + " " + TagLoop.ATTR_TYPE + "] must be defined.");
		}
		return tagLoop;
	}

	private TagIf createTagIf(Tag tag) throws BaseException {
		Map<String, String> attributes = new HashMap<String, String>(tag.getAttributes());
		TagIf tagIf = new TagIf();

		if (attributes.containsKey(TagIf.ATTR_NAME)) {
			String attributeValue = attributes.remove(TagIf.ATTR_NAME);
			if (!isNameParameter(attributeValue)) {
				throw new TemplateConfigurationException(null, "Cannot evaluate the attribute [" + tag.getName() + " "
						+ TagIf.ATTR_NAME + "] as a parameter name");
			}
			tagIf.setName(attributeValue);
		}
		if (attributes.containsKey(TagIf.ATTR_VALUE)) {
			String attributeValue = attributes.remove(TagIf.ATTR_VALUE);
			tagIf.setValue(attributeValue);
		}
		if (attributes.containsKey(TagIf.ATTR_INDEXES)) {
			String attributeValue = attributes.remove(TagIf.ATTR_INDEXES);
			String[] indexes = toArray(attributeValue);
			if (indexes == null) {
				throw new TemplateConfigurationException(null, "Cannot evaluate the value of the attribute '"
						+ TagParameter.ATTR_INDEXES + "' as a comma separated list of index names: " + tag.getName());
			}
			tagIf.setIndexes(indexes);
		}
		if (attributes.containsKey(TagIf.ATTR_TYPE)) {
			String attributeValue = attributes.remove(TagIf.ATTR_TYPE);
			try {
				TagIf.Type valueType = TagIf.Type.valueOf(attributeValue);
				tagIf.setType(valueType);
			} catch (Exception ex) {
				throw new TemplateConfigurationException(null,
						"The value of the attribute [" + tag.getName() + " " + TagIf.ATTR_TYPE + "] must be one of: " //
								+ UtilsString.arrayToString(TagIf.Type.values(), null, null, ","));
			}
		}

		if (!attributes.isEmpty()) {
			throw new TemplateConfigurationException(null,
					"Unknown attribute " + attributes.keySet().iterator().next() + " in the tag " + TagIf.TAG);
		}

		if (tagIf.getIndexes() == null) {
			tagIf.setIndexes(new String[0]);
		}
		if (tagIf.getName() == null) {
			throw new TemplateConfigurationException(null,
					"Attribute [" + TagIf.TAG + " " + TagIf.ATTR_NAME + "] must be defined.");
		}
		if (tagIf.getType() == null) {
			throw new TemplateConfigurationException(null,
					"Attribute [" + TagIf.TAG + " " + TagIf.ATTR_NAME + "] must be defined.");
		} else if ((tagIf.getType() == TagIf.Type.EQ || tagIf.getType() == TagIf.Type.NE) && tagIf.getValue() == null) {
			throw new TemplateConfigurationException(null,
					"The attribut " + TagIf.ATTR_VALUE + " is expected when the value of the attribute ["
							+ tag.getName() + " " + TagIf.ATTR_TYPE + "=\"" + tagIf.getType() + "\"]");
		} else if ((tagIf.getType() == TagIf.Type.EMPTY || tagIf.getType() == TagIf.Type.NONEMPTY)
				&& tagIf.getValue() != null) {
			throw new TemplateConfigurationException(null,
					"The attribut " + TagIf.ATTR_VALUE + " may not be used when the value of the attribute ["
							+ tag.getName() + " " + TagIf.ATTR_TYPE + "=\"" + tagIf.getType() + "\"]");
		}
		return tagIf;
	}

	private TagEnd createTagEnd(Tag tag) throws BaseException {
		if (!tag.getAttributes().isEmpty()) {
			throw new TemplateConfigurationException(null, "Unknown attribute '"
					+ tag.getAttributes().keySet().iterator().next() + "' for the tag '" + TagEnd.TAG + "'");
		}
		return new TagEnd();
	}

	private Part createTag(Tag tag) throws BaseException {
		if (TagParameter.TAG.equals(tag.getName())) {
			return createTagParameter(tag);
		} else if (TagIndex.TAG.equals(tag.getName())) {
			return createTagIndex(tag);
		} else if (TagLoop.TAG.equals(tag.getName())) {
			return createTagLoop(tag);
		} else if (TagIf.TAG.equals(tag.getName())) {
			return createTagIf(tag);
		} else if (TagEnd.TAG.equals(tag.getName())) {
			return createTagEnd(tag);
		} else {
			throw new TemplateConfigurationException(null, "Unknown tag '" + tag.getName() + "'");
		}
	}

	private Long toLong(Value value) {
		if (value instanceof ValueText) {
			try {
				return Long.parseLong(((ValueText) value).getValue());
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (value instanceof ValueInteger) {
			return ((ValueInteger) value).getValue();
		}
		if (value instanceof ValueDecimal) {
			double valueDecimal = ((ValueDecimal) value).getValue();
			long valueInt = (long) valueDecimal;
			if (valueDecimal == (double) valueInt) {
				return valueInt;
			}
		}
		return null;
	}

	private Integer toInteger(Value value) {
		if (value instanceof ValueText) {
			try {
				return Integer.parseInt(((ValueText) value).getValue());
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		if (value instanceof ValueInteger) {
			return ((ValueInteger) value).getValue().intValue();
		}
		if (value instanceof ValueDecimal) {
			double valueDecimal = ((ValueDecimal) value).getValue();
			int valueInt = (int) valueDecimal;
			if (valueDecimal == (double) valueInt) {
				return valueInt;
			}
		}
		return null;
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
				throw new TemplateConfigurationException(null,
						"Attribute " + attribute.getName() + " exists already in the tag " + tag.getName());
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
		// ValueBase
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
			if (j == 0 && !Character.isJavaIdentifierStart(c)
					|| !Character.isJavaIdentifierPart(c) && c != '-' && c != '.') {
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
