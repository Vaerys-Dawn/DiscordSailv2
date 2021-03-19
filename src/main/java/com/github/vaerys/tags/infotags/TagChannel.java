package com.github.vaerys.tags.infotags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.entities.TextChannel;

public class TagChannel extends TagObject {

    public TagChannel(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        TextChannel channel = command.guild.getChannelByID(Utility.stringLong(getContents(from)));
        if (channel != null) {
            return replaceFirstTag(from, channel.getAsMention());
        } else {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    public String tagName() {
        return "<messageChannel>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "ChannelID";
    }

    @Override
    public String desc() {
        return "Replaces the tag with the messageChannel mention of the Id specified.";
    }
}
