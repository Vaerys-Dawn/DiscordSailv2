package com.github.vaerys.tags.cctags;

import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;

public class TagAuthor extends TagObject {

    public TagAuthor(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, Utility.prepArgs(command.user.displayName));
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
