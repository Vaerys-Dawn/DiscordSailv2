package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetMutedRole extends Command{
    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.setRoleToMentionID(-1);
            return "> Muted Role Removed.";
        }
        IRole role = Utility.getRoleFromName(args, command.guild.get());
        if (role == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            command.guild.config.setMutedRoleID(role.getLongID());
            return "> The role **" + role.getName() + "** Is now set as the mute role.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetMutedRole","MutedRole"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Muted role.";
    }

    @Override
    public String usage() {
        return "[Role Name/Remove]";
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
        return new Permissions[]{Permissions.MANAGE_ROLES,Permissions.MANAGE_SERVER};
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
    public void init() {

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
