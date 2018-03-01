package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.parts.TagIf;

public class If implements Block {
	private TagIf tag;
	private Content content;

	public TagIf getTag() {
		return tag;
	}

	public void setTag(TagIf tag) {
		this.tag = tag;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
}
