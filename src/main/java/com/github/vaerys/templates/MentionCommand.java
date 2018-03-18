package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MentionCommand extends Command {

    @Override
    public String getCommand(CommandObject command) {
        return "@" + command.client.bot.name + " " + names[0];
    }

    @Override
    public String getCommand(CommandObject command, int i) {
        return "@" + command.client.bot.name + " " + names[i];
    }

    @Override
    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        if (mention.getRest() == null) {
            return false;
        }
        List<IUser> users = command.message.getMentions();
        String error = "> Hello, sorry to interrupt you but i have just encountered a bug, let my developer know by sending me a dm with the following: ";
        String errorSuffix = "ChannelID: " + command.channel.longID + ", MessageID: " + command.message.longID +
                ", UserID: " + command.user.longID + ", GuildID: " + command.guild.longID + ", Timestamp: " + (command.message.getTimestamp().toEpochSecond() * 1000);
        if (command.client == null) {
            RequestHandler.sendMessage(error + "\n```\ncommand.client == null\n" + errorSuffix + "```", command);
            return false;
        }
        if (command.client.bot == null) {
            RequestHandler.sendMessage(error + "\n```\ncommand.client.bot == null\n" + errorSuffix + "```", command);
            return false;
        }
        IUser bot = command.client.bot.get();
        if (bot == null || !users.contains(bot)) {
            return false;
        }
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        for (String s : names) {
            String regex = "^(<@|<@!)" + command.client.bot.longID + "> " + s.toLowerCase();
            String toMatch = mention.getFirstWord() + " " + call.getFirstWord().toLowerCase();
            Matcher matcher = Pattern.compile(regex).matcher(toMatch);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getArgs(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }


    @Override
    public boolean isName(String args, CommandObject command) {
        String prefix = command.guild.config.getPrefixCommand();
        for (String s : names) {
            if (s.equalsIgnoreCase(args) || args.equalsIgnoreCase(prefix + s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void init() {

    }
}
