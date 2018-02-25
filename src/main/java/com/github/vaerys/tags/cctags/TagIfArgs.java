package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagIfArgs extends TagObject {

    public TagIfArgs(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitString = getSplit(from);
        if (StringUtils.containsIgnoreCase(args, splitString.get(0))) {
            from = replaceFirstTag(from, splitString.get(1));
        } else {
            from = replaceFirstTag(from, splitString.get(2));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifArgs>";
    }

    @Override
    public int argsRequired() {
        return 3;
    }

    @Override
    public String usage() {
        return "Contains" + splitter + "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the tag with true if args contains the first arguments and false if it does not.";
    }
}
