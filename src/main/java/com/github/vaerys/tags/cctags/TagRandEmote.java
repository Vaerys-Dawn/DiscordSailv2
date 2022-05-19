package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.entities.Emote;

import java.util.List;
import java.util.Random;

public class TagRandEmote extends TagObject {

    public TagRandEmote(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<Emote> emojis = command.guild.get().getEmotes();
        Random random = Globals.getGlobalRandom();
        Emote emoji = emojis.get(random.nextInt(emojis.size()));
        String output;
        if (emoji.getImageUrl().endsWith(".gif")) {
            output = "<a:" + emoji.getName() + ":" + emoji.getIdLong() + ">";
        } else {
            output = emoji.getAsMention();
        }
        return replaceFirstTag(from, output);
    }

    @Override
    public String tagName() {
        return "<randEmote>";
    }

    @Override
    public int argsRequired() {
        return 0;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String desc() {
        return "replaces itself with a random Custom Emoji that belongs to the current server.";
    }
}
