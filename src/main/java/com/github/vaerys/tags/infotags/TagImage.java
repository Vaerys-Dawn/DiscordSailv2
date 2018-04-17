package com.github.vaerys.tags.infotags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.EmptyTagObject;

public class TagImage extends EmptyTagObject {

    public TagImage(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    protected String tagName() {
        return "<image>";
    }

    @Override
    protected int argsRequired() {
        return 1;
    }

    @Override
    protected String usage() {
        return "ImageName.format";
    }

    @Override
    protected String desc() {
        return "Sends the image specified from the internal storage. Will fail if the path is wrong or the file doesn't exist.";
    }
}
