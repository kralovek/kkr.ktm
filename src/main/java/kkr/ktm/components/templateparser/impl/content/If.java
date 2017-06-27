package kkr.ktm.components.templateparser.impl.content;

import kkr.ktm.components.templateparser.impl.parts.TagIf;

public class If implements Block {
    private TagIf tag;
    private Content content;

    public TagIf getTag() {
        return tag;
    }

    public void setTag(final TagIf pTag) {
        this.tag = pTag;
    }

    public Content getContent() {
        return content;
    }
    public void setContent(final Content pContent) {
        content = pContent;
    }
}
