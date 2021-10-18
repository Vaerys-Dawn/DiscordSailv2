package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagUsername extends TagObject {

    public TagUsername(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, Utility.prepArgs(command.user.name));
    }

    @Override
    public String tagName() {
        return "<username>";
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
        return "Replaces itself with the username of the globalUser running the command.";
    }
}
