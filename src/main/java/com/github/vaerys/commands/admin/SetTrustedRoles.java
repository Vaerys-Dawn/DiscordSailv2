package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetTrustedRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);

        IRole role = Utility.getRoleFromName(split.getRest(), command.guild.get());
        if (role == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        }
        if (Utility.testUserHierarchy(command.user.get(), role, command.guild.get())) {
            if (command.guild.config.isRoleTrusted(role.getLongID())) {
                ListIterator iterator = command.guild.config.getTrustedRoleIDs().listIterator();
                while (iterator.hasNext()) {
                    IRole trustedRole = command.guild.get().getRoleByID((long) iterator.next());
                    if (role.getLongID() == trustedRole.getLongID()) {
                        iterator.remove();
                    }
                }
                return "> The **" + role.getName() + "** is no longer trusted.";
            } else {
                command.guild.config.getTrustedRoleIDs().add(role.getLongID());
                return "> The **" + role.getName() + "** is now trusted.";
            }
        } else {
            return "> You do not have permission to modify this role.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"TrustedRole"};
    }

    @Override
    public String description() {
        return "add or remove a Trusted role.";
    }

    @Override
    public String usage() {
        return "[Role name]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_ROLES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
