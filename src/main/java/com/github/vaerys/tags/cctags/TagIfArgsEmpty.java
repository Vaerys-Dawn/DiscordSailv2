package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

import java.util.List;

public class TagIfArgsEmpty extends TagObject {

    public TagIfArgsEmpty(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitString = getSplit(from);
        if (args == null || args.isEmpty()) {
            from = replaceFirstTag(from, splitString.get(0));
        } else {
            from = replaceFirstTag(from, splitString.get(1));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifArgsEmpty>";
    }

    @Override
    public int argsRequired() {
        return 2;
    }

    @Override
    public String usage() {
        return "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the tag with true if args are empty and false if they are not.";
    }
}
