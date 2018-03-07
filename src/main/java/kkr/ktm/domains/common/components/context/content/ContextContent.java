package kkr.ktm.domains.common.components.context.content;

import kkr.ktm.domains.common.components.context.index.ContextIndex;
import kkr.ktm.domains.common.components.context.level.ContextLevel;

public class ContextContent extends ContextLevel {
	private ContextIndex contextIndex;

	public ContextContent() {
		this.contextIndex = new ContextIndex();
	}

	public Object getParameter(String name, Integer... indexValues) {
		try {
			return contextIndex.getIndex(name);
		} catch (Exception ex) {
			return super.getParameter(name, indexValues);
		}
	}

	public ContextIndex getContextIndex() {
		return contextIndex;
	}
}
