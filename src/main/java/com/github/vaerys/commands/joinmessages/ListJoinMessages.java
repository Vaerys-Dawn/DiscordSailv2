package com.github.vaerys.commands.joinmessages;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.JoinMessage;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.LinkedList;
import java.util.List;

public class ListJoinMessages extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);

        StringHandler handler = new StringHandler();
        List<JoinMessage> messages = command.guild.channelData.getJoinMessages();
        if (messages.size() == 0) {
            return "> No Messages exist right now, you can create some with **" + new NewJoinMessage().getUsage(command) + "**";
        }
        int page = 1;
        try {
            page = Integer.parseInt(args);
            if (page <= 0) return "> Invalid Page.";
        } catch (NumberFormatException e) {
            if (args != null && !args.isEmpty()) {
                return "> Not a valid number";
            }
        }
        page--;

        List<String> pages = new LinkedList<>();

        int index = 1;
        int i = 0;
        for (JoinMessage m : messages) {
            if (i == 10) {
                i = 0;
                pages.add(handler.toString());
                handler.emptyContent();
            }
            String shortNote = Utility.truncateString(Utility.removeFun(m.getContent()), 65);
            handler.append("**> Message #" + index + "**");
            handler.append("\n" + shortNote);
            handler.append("\n");
            i++;
            index++;
        }
        pages.add(handler.toString());
        if (page >= pages.size()) {
            return "> Invalid Page.";
        }

        builder.withTitle("> Join Message list");
        builder.withDesc(pages.get(page) + "\n\n" + missingArgs(command));
        builder.withFooterText("Page " + (page + 1) + "/" + pages.size() + " | Total Join Messages: " + messages.size());
        builder.send(command.channel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"ListJoinMessages", "JoinMessages"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists all custom join messages";
    }

    @Override
    protected String usage() {
        return "(Page)";
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
        return new Permissions[0];
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
