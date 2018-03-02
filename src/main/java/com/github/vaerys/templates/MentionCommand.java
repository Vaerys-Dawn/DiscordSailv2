package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        if (!users.contains(command.client.bot.get())) {
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
