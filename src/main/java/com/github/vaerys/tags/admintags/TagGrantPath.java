package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

public class TagGrantPath extends TagAdminSubTagObject {

    public TagGrantPath(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        cc.grantKey(getSubTag(from), command);
        return removeFirstTag(from);
    }

    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    public String tagName() {
        return "grantPath";
    }

    @Override
    protected int argsRequired() {
        return 0;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected String desc() {
        return "Grants a Path key with the SubTag as the name. Removes itself upon activation.";
    }
}
