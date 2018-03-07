package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.Position;
import kkr.ktm.domains.common.components.parametersformater.template.error.ContentParseException;

public interface Open extends Content {
	void addContent(Content content) throws ContentParseException;

	Position getPosition();
}
