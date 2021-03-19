package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;
import net.dv8tion.jda.api.entities.Role;

public class TagRemoveRole extends TagAdminSubTagObject {

    public TagRemoveRole(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        Role role = GuildHandler.getRoleFromName(getSubTag(from), command.guild.get());
        if (role == null) {
            return replaceFirstTag(from, error);
        } else if (!Utility.testUserHierarchy(command.botUser.getMember(), role, command.guild.get())) {
            return removeAllTag(from);
        } else {
            if (command.user.roles.contains(role)) {
                command.guild.get().removeRoleFromMember(command.user.getMember(), role).queue();
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
        return "Removes the role specified in the Subtag from the globalUser upon activation. Replaces itself with \"You have had the **RoleName** role removed\".";
    }
}
