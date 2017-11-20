package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;

public class TagRemoveSanitizeTag extends TagObject {

    public TagRemoveSanitizeTag(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return removeAllTag(from);
    }

    @Override
    public String tagName() {
        return "<dontSanitize>";
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
        return "this tag is used to tell the <args> tag not to sanitize the input.";
    }
}
