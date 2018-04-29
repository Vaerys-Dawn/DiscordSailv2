package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

public class TagRemovePath extends TagAdminSubTagObject {

    public TagRemovePath(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        cc.removeKey(getSubTag(from), command);
        return removeFirstTag(from);
    }

    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    protected String tagName() {
        return "removePath";
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
        return "Removes a Path key with the SubTag as the name. Removes itself upon activation.";
    }
}
