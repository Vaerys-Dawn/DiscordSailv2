package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagAdminReplaceObject;

import java.util.LinkedList;
import java.util.List;

public class TagPathReplace extends TagAdminReplaceObject {

    public TagPathReplace(int priority, TagType... types) {
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

    public String execute(String from, CommandObject command, String args, AdminCCObject cc, List<ReplaceObject> toReplace) {
        String subTag = getSubTag(from);

        List<String> split = getSplit(from);

        if (cc.getKeysUser(command.user.longID).size() == 0 && subTag.equalsIgnoreCase("Start")) {
            toReplace.add(new ReplaceObject(split.get(0), split.get(1)));
            from = removeFirstTag(from);
        } else if (cc.searchKeys(subTag, command) && args.equalsIgnoreCase(subTag)) {
            toReplace.add(new ReplaceObject(split.get(0), split.get(1)));
            from = removeFirstTag(from);
            cc.removeKey(subTag, command);
            from = get(TagNoPath.class).removeAllTag(from);
        } else {
            from = removeFirstTag(from);
        }
        return from;
    }

    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    public String tagName() {
        return "pathReplace";
    }

    @Override
    protected int argsRequired() {
        return 2;
    }

    @Override
    protected String usage() {
        return "ReplaceThis" + splitter + "WithThis";
    }

    @Override
    protected String desc() {
        return "If the globalUser has the Path Key specifies in the SubTag it will replace the first variable with the second " +
                "variable and remove itself, else it will just remove itself.\n" +
                "However if the SubTag is set to \"Start\" it will act as if the globalUser has that key unless they have any other keys.\n" +
                "When activated the tag will remove any keys the globalUser may have to the path.";
    }

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
