package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class AddProfile extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Member user = null;
        try {
            long userID = Long.parseUnsignedLong(args);
            user = command.guild.getUserByID(userID);

        } catch (NumberFormatException e) {
            if (command.message.get().getMentions().size() != 0) {
                user = command.message.get().getMentionedMembers().get(0);
            }
        }
        if (command.guild.users.getUserByID(user.getIdLong()) != null) {
            return "\\> " + user.getNickname() + " already has a profile.";
        }
        if (user == null) return "\\> Invalid UserID";
        command.guild.users.addUser(user.getIdLong());
        return "\\> Profile for **" + user.getNickname() + "** Created.";
    }

    @Override
    protected String[] names() {
        return new String[]{"AddProfile"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows Admins to initiate a profile for a globalUser using a userID";
    }

    @Override
    protected String usage() {
        return "[User ID/@Mention]";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MESSAGE_MANAGE};
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
