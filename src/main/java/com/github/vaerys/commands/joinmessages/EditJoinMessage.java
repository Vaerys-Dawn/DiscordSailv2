package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.JoinMessage;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class EditJoinMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        int index;
        SplitFirstObject splitArgs = new SplitFirstObject(args);
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        try {
            index = Integer.parseInt(splitArgs.getFirstWord());
        } catch (NumberFormatException e) {
            return "\\> Not a valid number.";
        }
        index--;
        if (index < 0 || index >= messages.size()) {
            return "\\> Could not find message.";
        }
        if (splitArgs.getRest() == null) {
            return "\\> Could not find any content to edit with.";
        }
        if (!splitArgs.getRest().contains("<user>")) return "\\> Could not find <user> tag.";
        for (JoinMessage m : messages) {
            if (m.getContent().equals(splitArgs.getRest())) {
                return "\\> New Contents matches another Custom join message's contents, cannot have duplicate messages.";
            }
        }
        JoinMessage message = messages.get(index);
        message.setContent(splitArgs.getRest());
        return "\\> Contents edited.";
    }

    @Override
    public String[] names() {
        return new String[]{"EditJoinMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit an already existing custom join message." +
                "**Available Tags:** \n" + Utility.listFormatter(TagList.getNames(TagType.JOIN_MESSAGES), true) + "\n";
    }

    @Override
    public String usage() {
        return "[Index] [New Content]";
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
    public Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}