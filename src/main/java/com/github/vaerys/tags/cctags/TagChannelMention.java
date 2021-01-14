package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;

public class TagChannelMention extends TagObject {

    public TagChannelMention(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return replaceAllTag(from, command.guildChannel.mention);
    }

    @Override
    public String tagName() {
        return "<messageChannel>";
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
        return "Replaces itself with the current messageChannel's name.";
    }
}
