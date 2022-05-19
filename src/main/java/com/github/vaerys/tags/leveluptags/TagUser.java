package com.github.vaerys.tags.leveluptags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagUser extends TagObject {

    public TagUser(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, command.user.get().getAsMention());
    }

    @Override
    public String tagName() {
        return "<user>";
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
        return "Replaces itself with a user mention of the current user.";
    }
}
