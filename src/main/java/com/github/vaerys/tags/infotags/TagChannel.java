package com.github.vaerys.tags.infotags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;
import sx.blah.discord.handle.obj.IChannel;

public class TagChannel extends TagObject {

    public TagChannel(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        IChannel channel = command.guild.getChannelByID(Utility.stringLong(contents(from)));
        if (channel != null) {
            return replaceFirstTag(from, channel.mention());
        } else {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    public String tagName() {
        return "<channel>";
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
        return "Replaces the tag with the channel mention of the Id specified.";
    }
}
