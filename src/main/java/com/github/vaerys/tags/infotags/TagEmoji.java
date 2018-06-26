package com.github.vaerys.tags.infotags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.IEmoji;

public class TagEmoji extends TagObject {

    public TagEmoji(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        IEmoji emoji = command.guild.getEmojiByName(getContents(from));
        if (emoji == null) {
            return replaceFirstTag(from, error);
        }
        return replaceFirstTag(from, emoji.toString());
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
