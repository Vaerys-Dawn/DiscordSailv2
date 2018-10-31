package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagAdminReplaceObject;

import java.util.Collections;
import java.util.List;

public class TagShuffleReplace extends TagAdminReplaceObject {

    public TagShuffleReplace(int priority, TagType... types) {
        super(priority, types);
    }

    public String execute(String from, CommandObject command, String args, AdminCCObject cc, List<ReplaceObject> toReplace) {
        try {
            List<String> split = getSplit(from);
            int subtag = Integer.parseInt(getSubTag(from));
            List<String> options = split.subList(2, split.size());
            if (options.size() < subtag) return replaceAllTag(from, error);
            Collections.shuffle(options);
            options = options.subList(0, subtag);
            StringHandler combined = new StringHandler();
            for (String s : options) {
                if (!combined.isEmpty()) {
                    combined.append(split.get(1));
                }
                combined.append(s);
            }
            toReplace.add(new ReplaceObject(split.get(0), combined.toString()));
            return removeFirstTag(from);
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
        return "shuffleReplace";
    }

    @Override
    protected int argsRequired() {
        return -3;
    }

    @Override
    protected String usage() {
        return "ReplaceThis" + splitter + "Separator" + splitter + "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    protected String desc() {
        return "Randomly selects an amount of variables based on the amount specified in the subtag and joins them together with the separator variable and then replaces the first variable with the complete set.";
    }
}
