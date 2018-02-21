package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagReplaceObject;
import com.github.vaerys.enums.TagType;

public class TagReplaceError extends TagReplaceObject {

    public TagReplaceError(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        from = from.replaceAll("#ERROR#:<(?i)[a-z!]*?>", contents(from));
        from = removeAllTag(from);
        return from;
    }

    @Override
    public String tagName() {
        return "<repError>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "ReplaceWith";
    }

    @Override
    public String desc() {
        return "Replaces any tag errors with the first argument.";
    }
}
