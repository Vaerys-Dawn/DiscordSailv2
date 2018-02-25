package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageTokenizer;

public class SetLevelUpReaction extends Command {

    protected static final String[] NAMES = new String[]{"SetLevelUpReaction"};
    protected static final String USAGE = "[Emoji]/Remove";
    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_EMOJIS};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;

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
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Reaction that the bot will post to the message a user sent to level up.";
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
