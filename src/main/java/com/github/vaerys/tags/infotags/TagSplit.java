package com.github.vaerys.tags.infotags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagEmptyObject;

public class TagSplit extends TagEmptyObject {

    public TagSplit(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    protected String tagName() {
        return "<split>";
    }

    @Override
    protected String desc() {
        return "When placed anywhere in the info.txt file it will create a new message starting at the tag.";
    }
}
