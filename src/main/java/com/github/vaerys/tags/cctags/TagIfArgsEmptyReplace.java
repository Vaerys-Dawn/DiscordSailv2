package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;

import java.util.List;

public class TagIfArgsEmptyReplace extends TagReplaceObject {

    public TagIfArgsEmptyReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitString = getSplit(from);
        from = removeFirstTag(from);
        if (args == null || args.isEmpty()) {
            toReplace.add(new ReplaceObject(splitString.get(0), splitString.get(1)));
        } else {
            toReplace.add(new ReplaceObject(splitString.get(0), splitString.get(2)));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifArgsEmptyReplace>";
    }

    @Override
    public int argsRequired() {
        return 3;
    }

    @Override
    public String usage() {
        return "Replace" + splitter + "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the first argument with true if args are empty and false if they are not.";
    }
}
