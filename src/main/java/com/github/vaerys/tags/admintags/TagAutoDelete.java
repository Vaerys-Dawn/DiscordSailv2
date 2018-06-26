package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

public class TagAutoDelete extends TagAdminSubTagObject {

    public TagAutoDelete(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        try {
            int time = Integer.parseInt(getSubTag(from));
            if (time < 1 || time > 5) return replaceFirstTag(from,error);
            return from;
        } catch (NumberFormatException e) {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    protected String subTagUsage() {
        return "TimeMins";
    }

    @Override
    public String tagName() {
        return "autoDelete";
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
        return "Sets the message sent to time out after a period of time, min 1 min, max 5 mins.\nTag currently will not work if there is a <embedImage> tag present.";
    }
}
