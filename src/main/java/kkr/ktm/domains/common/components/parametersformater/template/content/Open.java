package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.Position;

public interface Open extends Content {
	void setContent(Content content);

	Position getPosition();
}
