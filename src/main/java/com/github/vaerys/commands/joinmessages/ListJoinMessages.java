package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.JoinMessage;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class ListJoinMessages extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("> Join Message list");
        StringHandler handler = new StringHandler();
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        if (messages.size() == 0) {
            return "> No Messages exist right now, you can create some with **" + new NewJoinMessage().getUsage(command) + "**";
        }
        int index = 1;
        for (JoinMessage m : messages) {
            String shortNote = Utility.truncateString(Utility.removeFun(m.getContent()), 65);
            handler.append("**> Message #" + index + "**");
            handler.append("\n" + shortNote);
            handler.append("\n");
            index++;
        }
        builder.withDesc(handler.toString());
        builder.send(command.channel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"ListJoinMessages","JoinMessages"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists all custom join messages";
    }

    @Override
    protected String usage() {
        return null;
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
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    protected void init() {

    }
}
