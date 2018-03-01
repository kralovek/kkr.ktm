package kkr.ktm.domains.common.components.parametersformater.template2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.common.utils.UtilsFile;
import kkr.ktm.domains.common.components.parametersformater.ParametersFormatter;
import kkr.ktm.domains.common.components.parametersformater.template2.content.Close;
import kkr.ktm.domains.common.components.parametersformater.template2.content.Content;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentBase;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentComposed;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentEnd;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentIf;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentLoop;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentNumber;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentParameter;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContentText;
import kkr.ktm.domains.common.components.parametersformater.template2.content.ContextContent;
import kkr.ktm.domains.common.components.parametersformater.template2.content.Open;
import kkr.ktm.domains.common.components.parametersformater.template2.error.ContentParseException;
import kkr.ktm.domains.common.components.parametersformater.template2.part.Part;
import kkr.ktm.domains.common.components.parametersformater.template2.part.PartTag;
import kkr.ktm.domains.common.components.parametersformater.template2.part.PartText;
import kkr.ktm.domains.common.components.parametersformater.template2.part.TagType;

public class ParametersFormatterTemplate2 extends ParametersFormatterTemplate2Fwk implements ParametersFormatter {
	private static final Logger LOG = Logger.getLogger(ParametersFormatterTemplate2.class);

	public String format(Content content, Map<String, Object> parameters) throws BaseException {
		LOG.trace("BEGIN");
		try {
			String retval;
			if (content != null) {
				ContextContent context = new ContextContent(parameters);
				retval = content.evaluate(context);
			} else {
				retval = "";
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public String format(String source, Map<String, Object> parameters) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Content content = parse(source);
			UtilsFile.contentToFile(content.toString(), new File("CONTENT.txt"));
			String retval = format(content, parameters);
			UtilsFile.contentToFile(retval, new File("RESULT.txt"));
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	public Content parse(String source) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<Part> parts = createParts(source);

			Collection<Content> contentsFlat = createContentFlat(parts);

			Content contentTree = createContentTree(contentsFlat);

			LOG.trace("OK");
			return contentTree;
		} finally {
			LOG.trace("END");
		}
	}

	private Content createContentTree(Collection<Content> contentsFlat) throws ContentParseException, BaseException {
		LOG.trace("BEGIN");
		try {
			int open = 0;

			Collection<Content> contentCurrent = new ArrayList<Content>();
			Collection<Content> contentBodyList = new ArrayList<Content>();
			Open contentOpen = null;

			for (Content content : contentsFlat) {
				if (content instanceof Open) {
					if (open == 0) {
						contentOpen = (Open) content;
						contentCurrent.add(contentOpen);
						contentBodyList.clear();
					}
					open++;
					continue;
				}
				if (content instanceof Close) {
					open--;
					if (open == 0) {
						Content contentTree = createContentTree(contentBodyList);
						contentOpen.setContent(contentTree);
						contentOpen = null;
					} else if (open < 0) {
						throw new ContentParseException(((Close) content).getPosition(),
								"Missing opening Tag for a closing one");
					}
					continue;
				}
				if (open != 0) {
					contentBodyList.add(content);
				} else {
					contentCurrent.add(content);
				}
			}

			if (open < 0) {
				throw new ContentParseException(contentOpen.getPosition(), "Missing closing Tag for an opening one");
			}

			Content contentRetval;
			if (contentCurrent.size() == 0) {
				contentRetval = null;
			} else if (contentCurrent.size() == 1) {
				contentRetval = contentCurrent.iterator().next();
			} else {
				ContentComposed contentComposed = null;
				for (Content content : contentCurrent) {
					if (contentComposed == null) {
						contentComposed = new ContentComposed(((ContentBase) content).getPosition());
					}
					contentComposed.addContent(content);
				}
				contentRetval = contentComposed;
			}

			LOG.trace("OK");
			return contentRetval;
		} finally {
			LOG.trace("END");
		}
	}

	private Collection<Content> createContentFlat(Collection<Part> parts) throws ContentParseException, BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<Content> contentsFlat = new ArrayList<Content>();

			for (Part part : parts) {
				if (part instanceof PartText) {
					PartText partText = (PartText) part;
					ContentText content = new ContentText(part.getPosition());
					content.setText(partText.getText());
					contentsFlat.add(content);
				} else //
				if (part instanceof PartTag) {
					PartTag partTag = (PartTag) part;
					try {
						TagType tagType = TagType.valueOf(partTag.getName());
						Content content;
						switch (tagType) {
						case PARAMETER: {
							content = new ContentParameter(part.getPosition(), partTag.getAttributes());
							break;
						}

						case NUMBER: {
							content = new ContentNumber(part.getPosition(), partTag.getAttributes(), expressionParser);
							break;
						}
						case IF: {
							content = new ContentIf(part.getPosition(), partTag.getAttributes());
							break;
						}
						case LOOP: {
							content = new ContentLoop(part.getPosition(), partTag.getAttributes());
							break;
						}
						case END: {
							content = new ContentEnd(part.getPosition(), partTag.getAttributes());
							break;
						}

						default:
							throw new ContentParseException(part.getPosition(),
									"Unsupported Tag: " + partTag.toString());
						}
						contentsFlat.add(content);
					} catch (IllegalArgumentException ex) {
						throw new ContentParseException(part.getPosition(), "Unknown Tag: " + partTag.toString());
					}
				} else {
					throw new ContentParseException(part.getPosition(),
							"Unsupported content Part: " + part.getClass().getName());
				}

			}
			LOG.trace("OK");
			return contentsFlat;
		} finally {
			LOG.trace("END");
		}
	}

	private Collection<Part> createParts(String source) throws ContentParseException {
		LOG.trace("BEGIN");
		try {
			Collection<Part> parts = new ArrayList<Part>();
			Position position = new Position();

			StringBuffer buffer = new StringBuffer(source);
			char[] chars = source.toCharArray();

			int iPos = 0;
			while (true) {
				//
				// TEXT
				//
				{
					int iPosStart = iPos;
					boolean escaped = false;
					for (; iPos < chars.length; iPos++) {
						if (escaped) {
							escaped = false;
							continue;
						}
						if (chars[iPos] == symbolEscape) {
							buffer.deleteCharAt(iPos);
							chars = buffer.toString().toCharArray();
							iPos--;
							escaped = true;
							continue;
						}
						if (chars[iPos] == symbolBracketOpen) {
							break;
						}
					}

					if (iPosStart != iPos) {
						PartText partText = new PartText(position.movePosition(iPos),
								buffer.substring(iPosStart, iPos));
						parts.add(partText);
					}
				}

				if (iPos >= chars.length) {
					break;
				}

				//
				// TAG
				//
				{
					PartTag partTag;
					int iPosStartTag = iPos;
					iPos++;
					iPos += countSpaces(chars, iPos);

					//
					// NAME
					//
					{
						if (isEnd(chars, iPos) || !isNameStart(chars[iPos])) {
							throw new ContentParseException(position.movePosition(iPos), "Tag name is expected");
						}
						int iPosStart = iPos;
						iPos += countName(chars, iPos);
						String tagName = buffer.substring(iPosStart, iPos);

						partTag = new PartTag(position.movePosition(iPosStartTag), tagName);
						parts.add(partTag);

						if (!isSpace(chars[iPos]) && !isEnd(chars, iPos) && chars[iPos] != symbolBracketClose) {
							throw new ContentParseException(position.movePosition(iPos),
									"Unexpected character in tag name: '" + chars[iPos] + "'");
						}

						iPos += countSpaces(chars, iPos);
					}

					if (chars[iPos] == symbolBracketClose) {
						iPos++;
						continue;
					}

					//
					// ATTRIBUTES
					//
					while (true) {
						String attributeName;
						{
							//
							// ATTRIBUTE-NAME
							//
							iPos += countSpaces(chars, iPos);

							if (isEnd(chars, iPos) || !isNameStart(chars[iPos])) {
								throw new ContentParseException(position.movePosition(iPos),
										"Attribute start is expected");
							}
							int iPosStart = iPos;
							iPos += countName(chars, iPos);
							attributeName = buffer.substring(iPosStart, iPos);

							iPos += countSpaces(chars, iPos);
						}
						{
							//
							// ATTRIBUTE-OPERATOR
							//
							if (chars[iPos] != '=') {
								throw new ContentParseException(position.movePosition(iPos), "Expected character '='");
							}

							iPos++;
						}
						String attributeValue;
						{
							//
							// ATTRIBUTE-VALUE
							//
							iPos += countSpaces(chars, iPos);

							if (isEnd(chars, iPos) || chars[iPos] != symbolQuote) {
								throw new ContentParseException(position.movePosition(iPos),
										"Expected opening character '" + symbolQuote + "'");
							}

							iPos++;
							int iPosStart = iPos;
							boolean escaped = false;
							for (; iPos < chars.length; iPos++) {
								if (escaped) {
									escaped = false;
									continue;
								}
								if (chars[iPos] == symbolEscape) {
									escaped = true;
									buffer.deleteCharAt(iPos);
									iPos--;
									chars = buffer.toString().toCharArray();
									continue;
								}
								if (chars[iPos] == symbolQuote) {
									break;
								}
							}

							if (chars[iPos] != symbolQuote) {
								throw new ContentParseException(position.movePosition(iPos),
										"Expected closing character '" + symbolQuote + "'");
							}

							attributeValue = buffer.substring(iPosStart, iPos);
							iPos++;

							if (!isSpace(chars[iPos]) && !isEnd(chars, iPos) && chars[iPos] != symbolBracketClose) {
								throw new ContentParseException(position.movePosition(iPos),
										"Unexpected character after attribute " + attributeName + " value: '"
												+ chars[iPos] + "'");
							}

							iPos += countSpaces(chars, iPos);
						}

						partTag.addAttribute(attributeName, attributeValue);

						if (chars[iPos] == symbolBracketClose) {
							iPos++;
							break;
						}
					}
				}
			}

			LOG.trace("OK");
			return parts;
		} finally {
			LOG.trace("END");
		}
	}

	private int countSpaces(char[] chars, int iPos) {
		int count = 0;
		for (; iPos < chars.length && isSpace(chars[iPos]); iPos++, count++) {
		}
		return count;
	}

	private int countName(char[] chars, int iPos) {
		int count = 0;
		for (; iPos < chars.length && isName(chars[iPos]); iPos++, count++) {
		}
		return count;
	}

	private boolean isEnd(char[] chars, int iPos) {
		return iPos >= chars.length;
	}

	private boolean isSpace(char c) {
		return Character.isWhitespace(c);
	}

	private boolean isNameStart(char c) {
		return false //
				|| c >= 'a' && c <= 'z' //
				|| c >= 'A' && c <= 'Z' //
				|| c == '_' //
		;
	}

	private boolean isName(char c) {
		return false //
				|| isNameStart(c) //
				|| c >= '0' && c <= '9' //
		;
	}
}
