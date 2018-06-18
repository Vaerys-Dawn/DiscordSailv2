package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

import java.util.Collections;
import java.util.List;

public class TagShuffle extends TagAdminSubTagObject {

    public TagShuffle(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        try {
            List<String> split = getSplit(from);
            int subtag = Integer.parseInt(getSubTag(from));
            List<String> options = split.subList(1, split.size());
            if (options.size() < subtag) return replaceAllTag(from, error);
            Collections.shuffle(options);
            options = options.subList(0, subtag);
            StringHandler combined = new StringHandler();
            for (String s : options) {
                if (!combined.isEmpty()) {
                    combined.append(split.get(0));
                }
                combined.append(s);
            }
            return replaceFirstTag(from, combined.toString());
        } catch (NumberFormatException e) {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    protected String subTagUsage() {
        return "Amount";
    }

    @Override
    public String tagName() {
        return "shuffle";
    }

    @Override
    protected int argsRequired() {
        return -2;
    }

    @Override
    protected String usage() {
        return "Separator" + splitter + "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    protected String desc() {
        return "Randomly selects an amount of variables based on the amount specified in the subtag and joins them together with the separator variable.";
    }
}
