package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;

public abstract class TagEmptyObject extends TagObject {

    public TagEmptyObject(int priority, TagType... types) {
        super(priority, types);
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
    public String execute(String from, CommandObject command, String args) {
        return null;
    }

    @Override
    public String handleTag(String from, CommandObject command, String args) {
        return from;
    }
}
