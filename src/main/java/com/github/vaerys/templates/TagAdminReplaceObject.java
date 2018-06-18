package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.ReplaceObject;

import java.util.LinkedList;
import java.util.List;

public abstract class TagAdminReplaceObject extends TagAdminSubTagObject {

    public TagAdminReplaceObject(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        List<ReplaceObject> toReplace = new LinkedList<>();
        String old = from;
        from = handleReplaceTag(from, command, args, cc);
        if (!from.equals(old)) {
            from = replaceMode(from, toReplace);
        }
        return from;
    }

    public abstract String execute(String from, CommandObject command, String args, AdminCCObject cc, List<ReplaceObject> toReplace);

    public String handleReplaceTag(String from, CommandObject command, String args, AdminCCObject cc) {
        //cloned from the super and updated to suit replaces
        List<ReplaceObject> toReplace = new LinkedList<>();
        String old = from;
        while (cont(from)) {
            int absoluteArgs = Math.abs(requiredArgs);
            if (requiredArgs == 0 ||
                    requiredArgs < 0 && getSplit(from).size() >= absoluteArgs ||
                    requiredArgs == getSplit(from).size()) {
                from = execute(from, command, args, cc, toReplace);
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
