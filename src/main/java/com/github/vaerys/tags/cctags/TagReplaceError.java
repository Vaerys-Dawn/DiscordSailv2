package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;

import java.util.List;

public class TagReplaceError extends TagReplaceObject {

    public TagReplaceError(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        from = from.replaceAll("#ERROR#:<(?i)[a-z!]*?>", getContents(from));
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
