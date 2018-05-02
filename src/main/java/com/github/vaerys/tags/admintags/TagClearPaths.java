package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminObject;

public class TagClearPaths extends TagAdminObject {

    public TagClearPaths(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        cc.removeAllKeys(command);
        return removeAllTag(from);
    }

    @Override
    protected String tagName() {
        return "clearAllPaths";
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
        return "This tag will clear all active path keys a user has. Removes itself upon activation.";
    }
}
