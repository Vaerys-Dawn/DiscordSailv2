package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.JoinMessage;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class JoinMessageInfo extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        int index;
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        XEmbedBuilder builder = new XEmbedBuilder(command);
        try {
            index = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            return "> Not a valid number.";
        }
        index--;
        if (index < 0 || index >= messages.size()) {
            return "> Could not find message.";
        }
        JoinMessage message = messages.get(index);
        builder.withTitle("Message #" + (index + 1));
        builder.withDesc(message.getContent());
        UserObject user = new UserObject(message.getCreator(), command.guild);
        if (user.get() == null) {
            builder.withFooterText("Could not find user.");
        } else {
            builder.withFooterText("Creator: @" + user.username);
            builder.withFooterIcon(user.getAvatarURL());
        }
        builder.send(command.channel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"JoinMessage", "JoinMessageInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to view info on a specific Custom Join message.";
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
        return false;
    }

    @Override
    protected void init() {

    }
}
