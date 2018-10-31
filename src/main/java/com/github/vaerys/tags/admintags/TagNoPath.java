package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminObject;

public class TagNoPath extends TagAdminObject {

    public TagNoPath(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        if (cc.getKeysUser(command.user.longID).size() != 0) {
            from = replaceFirstTag(from, contents(from));
        } else {
            from = removeFirstTag(from);
        }
        return from;
    }

    @Override
    protected String tagName() {
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
