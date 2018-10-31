package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.utils.TriVar;
import com.github.vaerys.templates.TagAdminObject;

import java.util.List;

public class TagNoPath extends TagAdminObject {

    public TagNoPath(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        List<TriVar<Long, String, Long>> keys = cc.getKeysUser(command.user.longID);
        boolean hasKeys = keys.size() != 0;
        boolean foundKey = false;
        for (TriVar<Long, String, Long> key : keys) {
            if (key.getVar2().equalsIgnoreCase(args)) {
                foundKey = true;
            }
        }
        if (hasKeys && !foundKey) {
            from = replaceFirstTag(from, getContents(from));
        } else {
            from = removeFirstTag(from);
        }
        return from;
    }

    @Override
    public String tagName() {
        return "noPath";
    }

    @Override
    protected int argsRequired() {
        return 1;
    }

    @Override
    protected String usage() {
        return "Contents";
    }

    @Override
    protected String desc() {
        return "If the user has Path Keys and no paths have been activated it will replace itself with the contents of the tag, else it will remove itself.";
    }
}
