package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminObject;

public class TagAddTry extends TagAdminObject {

    public TagAddTry(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        if (cc.hasLimitTry()) {
            command.guild.adminCCs.addTry(command.user.longID, cc.getName());
        }
        return removeAllTag(from);
    }

    @Override
    protected String tagName() {
        return "addTry";
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
        return "Add's the user to the list of attempts when the tag is run. Will only work if the Admin CC contains the <LimitTry> Tag. Removes itself upon activation.";
    }
}
