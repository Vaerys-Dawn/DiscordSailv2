package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminObject;

import java.util.stream.Collectors;

public class TagListKeys extends TagAdminObject {

    public TagListKeys(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        String keyList = String.join(", ", cc.getKeysUser(command.user.longID).stream().map(k -> k.getVar2()).collect(Collectors.toList()));
        return replaceAllTag(from, keyList);
    }

    @Override
    public String tagName() {
        return "listKeys";
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
        return "replaces itself with a list of all active keys the user has.";
    }
}
