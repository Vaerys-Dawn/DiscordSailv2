package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.JoinMessage;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class DeleteJoinMessage extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        int index;
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        try {
            index = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            return "> Not a valid number.";
        }
        index--;
        if (index < 0 || index >= messages.size()) {
            return "> Could not find message.";
        }
        messages.remove(index);
        return "> Message removed.";
    }

    @Override
    protected String[] names() {
        return new String[]{"DeleteJoinMessage", "RemoveJoinMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to delete a already existing custom join message";
    }

    @Override
    protected String usage() {
        return "[Message Index]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CUSTOM_JOIN_MESSAGES;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
