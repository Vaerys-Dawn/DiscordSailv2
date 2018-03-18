package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;

import java.util.LinkedList;
import java.util.List;

public class TagRegex extends TagReplaceObject {

    public TagRegex(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitArgs = getSplit(from);
        from = removeFirstTag(from);
        toReplace.add(new ReplaceObject(splitArgs.get(0), splitArgs.get(1)));
        return from;
    }

    @Override
    public String tagName() {
        return "<regex>";
    }

    @Override
    public int argsRequired() {
        return 2;
    }

    @Override
    public String usage() {
        return "Regex" + splitter + "ReplaceWith";
    }

    @Override
    public String desc() {
        return "Uses a regular expression to replace parts of the command with the second argument.";
    }

    @Override
    public String replaceMode(String from, List<ReplaceObject> toReplace) {
        for (ReplaceObject t : toReplace) {
            from = from.replaceAll(t.getFrom(), t.getTo());
        }
        return from;
    }
}
