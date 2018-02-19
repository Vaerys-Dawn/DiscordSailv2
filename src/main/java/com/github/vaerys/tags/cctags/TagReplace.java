package com.github.vaerys.tags.cctags;

import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import com.github.vaerys.templates.TagType;

public class TagReplace extends TagReplaceObject {

    public TagReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitArgs = getSplit(from);
        from = removeFirstTag(from);
        toReplace.add(new ReplaceObject(splitArgs.get(0), splitArgs.get(1)));
        return from;
    }

    @Override
    public String tagName() {
        return "<replace>";
    }

    @Override
    public int argsRequired() {
        return 2;
    }

    @Override
    public String usage() {
        return "ReplaceThis" + splitter + "WithThis";
    }

    @Override
    public String desc() {
        return "Replaces the first argument with the second argument.";
    }
}
