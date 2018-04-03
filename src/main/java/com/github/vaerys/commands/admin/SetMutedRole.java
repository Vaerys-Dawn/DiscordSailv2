package com.github.vaerys.commands.admin;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetMutedRole extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.setRoleToMentionID(-1);
            return "> Muted Role Removed.";
        }
        IRole role = GuildHandler.getRoleFromName(args, command.guild.get());
        if (role == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            command.guild.config.setMutedRoleID(role.getLongID());
            return "> The role **" + role.getName() + "** is now set as the mute role.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetMutedRole", "MutedRole"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Muted role.";
    }

    @Override
    protected String usage() {
        return "[Role Name/Remove]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_ROLES, Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
