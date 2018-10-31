package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.TagObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagSingleArgs extends TagObject {

    public TagSingleArgs(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        if (!from.contains("<dontSanitize>")) {
            args = Utility.prepArgs(args);
        }
        String[] split = args.split(" ");
        List<String> splitArgs;
        if (split == null) {
            splitArgs = new ArrayList<>();
        } else {
            splitArgs = Arrays.asList(args.split(" "));
        }
        try {
            int position = Integer.parseInt(getContents(from)) - 1;
            if (position >= splitArgs.size() || position < 0) {
                from = removeFirstTag(from);
            } else {
                from = replaceFirstTag(from, splitArgs.get(position));
            }
        } catch (NumberFormatException e) {
            from = replaceFirstTag(from, error);
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<args!>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "ArgsPosition";
    }

    @Override
    public String desc() {
        return "replaces the tag with the word at the position of the first argument.";
    }
}
