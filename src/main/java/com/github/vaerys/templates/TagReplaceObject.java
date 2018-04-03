package com.github.vaerys.templates;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.ReplaceObject;

import java.util.LinkedList;
import java.util.List;

public abstract class TagReplaceObject extends TagObject {

    @Override
    public String execute(String from, CommandObject command, String args) {
        return null;
    }

    public abstract String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace);

    public TagReplaceObject(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String handleTag(String from, CommandObject command, String args) {
        //cloned from the super and updated to suit replaces
        List<ReplaceObject> toReplace = new LinkedList<>();
        String old = from;
        while (cont(from)) {
            int absoluteArgs = Math.abs(requiredArgs);
            if (requiredArgs == 0) {
                from = execute(from, command, args, toReplace);
            } else if (requiredArgs < 0 && getSplit(from).size() >= absoluteArgs) {
                from = execute(from, command, args, toReplace);
            } else if (requiredArgs == getSplit(from).size()) {
                from = execute(from, command, args, toReplace);
            } else {
                from = replaceFirstTag(from, error);
            }
            if (from == null) from = "";
        }
        if (!from.equals(old)) {
            from = replaceMode(from, toReplace);
        }
        return from;
    }

    public String replaceMode(String from, List<ReplaceObject> toReplace) {
        for (ReplaceObject r : toReplace) {
            from = from.replace(r.getFrom(), r.getTo());
        }
        return from;
    }
}
