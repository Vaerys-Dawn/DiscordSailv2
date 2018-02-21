package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.enums.TagType;

public class TagAuthor extends TagObject {

    public TagAuthor(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, command.user.displayName);
    }

    @Override
    public String tagName() {
        return "<author>";
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
        return "Replaces itself with the display name of the user running the command.";
    }
}
