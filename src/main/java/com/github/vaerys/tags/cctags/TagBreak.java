package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagBreak extends TagObject {

    public TagBreak(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from,"\n");
    }

    @Override
    protected String tagName() {
        return "<br>";
    }

    @Override
    protected int argsRequired() {
        return 0;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected String desc() {
        return "Replaces itself with a newline character.";
    }
}
