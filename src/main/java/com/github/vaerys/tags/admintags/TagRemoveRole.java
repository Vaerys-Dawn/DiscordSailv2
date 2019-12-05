package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;
import sx.blah.discord.handle.obj.IRole;

public class TagRemoveRole extends TagAdminSubTagObject {

    public TagRemoveRole(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        IRole role = GuildHandler.getRoleFromName(getSubTag(from), command.guild.get());
        if (role == null) {
            return replaceFirstTag(from, error);
        } else if (!Utility.testUserHierarchy(command.client.bot.get(), role, command.guild.get())) {
            return removeAllTag(from);
        } else {
            if (command.user.roles.contains(role)) {
                RequestHandler.roleManagement(command, role, false);
                from = replaceFirstTag(from, "You have had the **" + role.getName() + "** role removed");
            }
            from = removeAllTag(from);
            return from;
        }
    }

    @Override
    protected String subTagUsage() {
        return "Role Name";
    }

    @Override
    public String tagName() {
        return "removeRole";
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
        return "Removes the role specified in the Subtag from the user upon activation. Replaces itself with \"You have had the **RoleName** role removed\".";
    }
}
