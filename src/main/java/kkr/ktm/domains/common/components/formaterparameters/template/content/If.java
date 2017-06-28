package kkr.ktm.domains.common.components.formaterparameters.template.content;

import kkr.ktm.domains.common.components.formaterparameters.template.parts.TagIf;

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
