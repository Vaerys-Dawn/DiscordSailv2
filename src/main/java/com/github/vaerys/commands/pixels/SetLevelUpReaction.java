package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import emoji4j.Emoji;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;

public class SetLevelUpReaction extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String emojiString = new SplitFirstObject(command.message.getContent()).getFirstWord();
        if (args.equalsIgnoreCase("Remove")) {
            command.guild.config.levelUpReaction = "null";
            return "\\> Level Up reaction was removed.";
        } else if (EmojiUtils.isEmoji(emojiString)) {
            String emoji = EmojiUtils.getEmoji(emojiString).getEmoji();
            command.guild.config.levelUpReaction = emoji;
            return "\\> The message a user level ups with will now be reacted with " + emoji + ".";
        } else if (command.message.get().getEmotes().size() > 0) {
            Emote emote = command.message.get().getEmotes().get(0);
            if (command.client.get().getEmoteById(emote.getId()) == null)
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
        return "Sets the Reaction that the bot will post to the message a globalUser sent to level up.";
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
        return new Permission[]{Permission.MANAGE_EMOTES};
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
