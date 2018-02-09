package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageTokenizer;

public class SetLevelUpReaction extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        MessageTokenizer messageTokenizer = new MessageTokenizer(command.message.get());
        Emoji emoji = EmojiManager.getByUnicode(new SplitFirstObject(args).getFirstWord());
        IEmoji customEmoji = null;
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.levelUpReaction = "null";
            return "> Level Up reaction was removed.";
        }
        if (messageTokenizer.hasNextEmoji()) {
            customEmoji = messageTokenizer.nextEmoji().getEmoji();
        }
        if (emoji == null && customEmoji == null) {
            return "> Not a valid Emoji.";
        } else if (emoji != null) {
            command.guild.config.levelUpReaction = emoji.getUnicode();
            return "> The message a user sent to level up will now be reacted with " + emoji.getUnicode() + ".";
        } else {
            command.guild.config.levelUpReaction = customEmoji.getStringID();
            return "> The message a user sent to level up will now be reacted with " + customEmoji.toString() + ".";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetLevelUpReaction"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Reaction that the bot will post to the message a user sent to level up.";
    }

    @Override
    public String usage() {
        return "[Emoji]/Remove";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_EMOJIS};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}