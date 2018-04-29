package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;
import sx.blah.discord.handle.obj.IRole;

public class TagGrantRole extends TagAdminSubTagObject {

    public TagGrantRole(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        IRole role = GuildHandler.getRoleFromName(getSubTag(from), command.guild.get());
        if (role == null) {
            return replaceFirstTag(from, error);
        } else if (!Utility.testUserHierarchy(command.client.bot, command.user, command.guild)) {
            return removeAllTag(from);
        } else {
            RequestHandler.roleManagement(command, role, true);
            from = replaceFirstTag(from,"You have been granted the **" + role.getName() + "** role");
            from = removeAllTag(from);
            return from;
        }
    }

    @Override
    protected String subTagUsage() {
        return "Role Name";
    }

    @Override
    protected String tagName() {
        return "grantRole";
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
        return "Grants the user the role specified in the Subtag upon activation. Replaces itself with \"You have been granted the **RoleName** role\".";
    }
}
