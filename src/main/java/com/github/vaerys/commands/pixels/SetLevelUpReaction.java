package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.fellbaum.jemoji.EmojiManager;

public class SetLevelUpReaction extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String emojiString = new SplitFirstObject(command.message.getContent()).getFirstWord();
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.levelUpReaction = "null";
            return "\\> Level Up reaction was removed.";
        } else if (EmojiManager.isEmoji(emojiString)) {
            command.guild.config.levelUpReaction = emojiString;
            return "\\> The message a user level ups with will now be reacted with " + emojiString + ".";
        } else if (command.message.get().getMentions().getCustomEmojis().size() > 0) {
            CustomEmoji emote = command.message.get().getMentions().getCustomEmojis().get(0);
            if (command.client.get().getEmojiById(emote.getId()) == null)
                return "\\> Not an Emoji that is on any of my guilds.";
            command.guild.config.levelUpReaction = emote.getId();
            return "\\> The message a user level ups with will now be reacted with " + emote.getAsMention() + ".";
        } else {
            return "\\> Not a valid Emoji.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetLevelUpReaction"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the Reaction that the bot will post to the message a user sent to level up.";
    }

    @Override
    protected String usage() {
        return "[Emoji]/Remove";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
