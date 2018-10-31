package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagIfArgsReplace extends TagReplaceObject {

    public TagIfArgsReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitString = getSplit(from);
        from = removeFirstTag(from);
        if (StringUtils.containsIgnoreCase(args, splitString.get(0))) {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(2)));
        } else {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(3)));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifArgsReplace>";
    }

    @Override
    public int argsRequired() {
        return 4;
    }

    @Override
    public String usage() {
        return "Contains" + splitter + "Replace" + splitter + "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the second argument with true if args contains the first argument and false if they do not.";
    }
}
