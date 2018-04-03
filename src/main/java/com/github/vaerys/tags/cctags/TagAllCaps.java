package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;

public class TagAllCaps extends TagObject {

    public TagAllCaps(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        from = from.toUpperCase();
        from = from.replace("<DELCALL>", "<delCall>");
        return from.replace("<EMBEDIMAGE>", "<embedImage>");
    }

    @Override
    public String tagName() {
        return "<toCaps>";
    }

    @Override
    public int argsRequired() {
        return 0;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String desc() {
        return "Sets the response to all caps.";
    }
}
