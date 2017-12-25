package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.IEmoji;

import java.util.List;
import java.util.Random;

public class TagRandEmote extends TagObject {

    public TagRandEmote(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<IEmoji> emojis = command.guild.get().getEmojis();
        Random random = new Random();
        return replaceFirstTag(from, emojis.get(random.nextInt(emojis.size())).toString());
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