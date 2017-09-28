package com.github.vaerys.interfaces;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

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
            if ((mention.getFirstWord() + " " + call.getFirstWord()).matches("^(<@|<@!)" + command.client.longID + "> " + s + " .*")) {
                return true;
            }
        }
        return false;
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
