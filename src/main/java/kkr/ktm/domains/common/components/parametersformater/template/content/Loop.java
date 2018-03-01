package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.parts.TagLoop;

public class Loop implements Block {
	private TagLoop tag;
	private Content content;

	public TagLoop getTag() {
		return tag;
	}

	public void setTag(TagLoop tag) {
		this.tag = tag;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
}
