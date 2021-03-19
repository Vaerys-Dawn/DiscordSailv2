package com.github.vaerys.tags.infotags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.entities.Emote;

import java.util.List;

public class TagEmoji extends TagObject {

    public TagEmoji(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<Emote> emoji = command.guild.getEmojiByName(getContents(from));
        if (emoji == null || emoji.isEmpty()) {
            return replaceFirstTag(from, error);
        }
        return replaceFirstTag(from, emoji.get(0).getAsMention());
    }

    @Override
    public String prefix() {
        return "#:";
    }

    @Override
    public String suffix() {
        return ":#";
    }

    @Override
    public String tagName() {
        return "<emoji>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "EmojiName";
    }

    @Override
    public String desc() {
        return "Replaces itself with the emoji of the same name.";
    }
}
