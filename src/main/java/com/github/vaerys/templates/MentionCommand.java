package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface MentionCommand extends Command {
    @Override
    default String getCommand(CommandObject command) {
        return "@" + command.client.bot.getName() + " " + names()[0];
    }

    default boolean isCall(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        if (mention.getRest() == null) {
            return false;
        }
        if (!command.message.get().getMentions().contains(command.client.bot)) {
            return false;
        }
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        for (String s : names()) {
            String regex = "^(<@|<@!)" + command.client.longID + "> " + s.toLowerCase();
            String toMatch = mention.getFirstWord() + " " + call.getFirstWord().toLowerCase();
            Matcher matcher = Pattern.compile(regex).matcher(toMatch);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    default String getArgs(String args, CommandObject command) {
        SplitFirstObject mention = new SplitFirstObject(args);
        SplitFirstObject call = new SplitFirstObject(mention.getRest());
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    @Override
    default String type() {
        return Command.TYPE_MENTION;
    }

    @Override
    default String dualType() {
        return null;
    }

    @Override
    default String dualDescription() {
        return null;
    }

    @Override
    default Permissions[] dualPerms() {
        return new Permissions[0];
    }

    @Override
    default String dualUsage() {
        return null;
    }
}
