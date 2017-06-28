package kkr.ktm.domains.common.components.parametersformater.template.content;

import kkr.ktm.domains.common.components.parametersformater.template.parts.TagLoop;

/**
 * Loop
 *
 * @author KRALOVEC-99999
 */
public class Loop implements Block {
    private TagLoop tag;
    private Content content;

    public TagLoop getTag() {
        return tag;
    }

    public void setTag(final TagLoop pTag) {
        this.tag = pTag;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(final Content pContent) {
        this.content = pContent;
    }
}
