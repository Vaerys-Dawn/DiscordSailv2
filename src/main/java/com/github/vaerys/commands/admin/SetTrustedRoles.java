package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetTrustedRoles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.setRoleToMentionID(-1);
            return "> Invite Allowed Role Removed.";
        }
        IRole role = GuildHandler.getRoleFromName(args, command.guild.get());
        if (role == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            command.guild.config.setInviteAllowed(role.getLongID());
            return "> The role **" + role.getName() + "** Is now set as the invite allowed role.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetInviteAllowed"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Invite allowed role that will allow users to bypass the invite approved check when the setting is enabled.";
    }

    @Override
    protected String usage() {
        return "[Role name]";
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
        return new Permissions[]{Permissions.MANAGE_ROLES};
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
