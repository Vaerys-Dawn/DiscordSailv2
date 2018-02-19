package com.github.vaerys.commands.admin;

import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetTrustedRoles extends Command {
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

    protected static final String[] NAMES = new String[]{"TrustedRole"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "add or remove a Trusted role.";
    }

    protected static final String USAGE = "[Role name]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_ROLES};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
