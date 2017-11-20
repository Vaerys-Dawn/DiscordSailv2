package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagSpacer extends TagObject {

    public TagSpacer(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, "\u200b");
    }

    @Override
    public String tagName() {
        return "<spacer>";
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
        return "Replaces itself with an invisible character that takes up no space.";
    }
}
