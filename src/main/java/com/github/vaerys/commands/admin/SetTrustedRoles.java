package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetTrustedRoles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);

        IRole role = GuildHandler.getRoleFromName(split.getRest(), command.guild.get());
        if (role == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        }
        if (Utility.testUserHierarchy(command.user.get(), role, command.guild.get())) {
            if (command.guild.config.isRoleTrusted(role.getLongID())) {
                ListIterator iterator = command.guild.config.getTrustedRoleIDs().listIterator();
                while (iterator.hasNext()) {
                    IRole trustedRole = command.guild.getRoleByID((long) iterator.next());
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
    protected String[] names() {
        return new String[]{"TrustedRole"};
    }

    @Override
    public String description(CommandObject command) {
        return "add or remove a Trusted role.";
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
