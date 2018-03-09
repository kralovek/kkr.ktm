package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;
import kkr.ktm.utils.parser.Position;

public interface Open extends Content {
	void addContent(Content content) throws ContentParseException;

	Position getPosition();
}
