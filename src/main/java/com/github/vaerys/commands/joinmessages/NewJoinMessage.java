package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.JoinMessage;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class NewJoinMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        if (!args.contains("<user>")) return "> Could not find <user> Tag";
        for (JoinMessage m : messages) {
            if (m.getContent().equals(args)) {
                return "> New Join Message Contents matches another Custom Join Message's contents, cannot have duplicate messages.";
            }
        }
        if (StringUtils.containsIgnoreCase(args, "@everyone") || StringUtils.containsIgnoreCase(args, "@here")) {
            return "> You cannot put everyone or here mentions in join messages.";
        }
        messages.add(new JoinMessage(command.user.longID, args));
        return "> New Custom Join Message added at index " + command.guild.channelData.getJoinMessages().size() + ".";
    }

    @Override
    public String[] names() {
        return new String[]{"NewJoinMessage", "AddJoinMessage", "CreateJoinMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to add a new custom join message.\n" +
                "**Available Tags:** \n" + Utility.listFormatter(TagList.getNames(TagType.JOIN_MESSAGES), true) + "\n";
    }

    @Override
    public String usage() {
        return "[Message]";
    }

    @Override
    public SAILType type() {
        return SAILType.CUSTOM_JOIN_MESSAGES;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
}