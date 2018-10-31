package com.github.vaerys.templates;

import com.github.vaerys.main.Client;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
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
//        if (command.client.bot == null) {
//            command.guild.sendDebugLog(command, "MENTION_COMMAND", "BOT_NULL", command.message.getContent());
//            return false;
//        }
        SplitFirstObject mention = new SplitFirstObject(args);
        if (mention.getRest() == null) {
            return false;
        }
        List<IUser> users = command.message.getMentions();
        IUser bot = Client.getClient().getOurUser();
        if (bot == null || !users.contains(bot)) {
            return false;
        }
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        for (String s : names) {
            String regex = "^(<@|<@!)" + bot.getLongID() + "> " + s.toLowerCase();
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
