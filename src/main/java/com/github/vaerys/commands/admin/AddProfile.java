package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class AddProfile extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            long userID = Long.parseUnsignedLong(args);
            IUser user = command.guild.getUserByID(userID);
            if (user == null) return "> Invalid UserID";
            command.guild.users.addUser(userID);
            return "> Profile for **" + user.getDisplayName(command.guild.get()) + "** Created.";
        } catch (NumberFormatException e) {
            if (command.message.get().getMentions().size() != 0) {
                IUser user = command.message.get().getMentions().get(0);
                command.guild.users.addUser(user.getLongID());
                return "> Profile for **" + user.getDisplayName(command.guild.get()) + "** Created.";
            }
            return "> Invalid UserID";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"AddProfile"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows Admins to initiate a profile for a user using a userID";
    }

    @Override
    protected String usage() {
        return "[User ID/@Mention]";
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
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
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
    protected void init() {

    }
}
