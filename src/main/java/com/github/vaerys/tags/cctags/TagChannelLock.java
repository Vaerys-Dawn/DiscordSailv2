package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TagChannelLock extends TagObject {

    public TagChannelLock(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitContents = getSplit(from);
        Pattern mention = Pattern.compile("<#[0-9]*>");
        List<IChannel> channels = new ArrayList<>(splitContents.size());
        for (String s : splitContents) {
            if (!mention.matcher(s).matches()) {
                return replaceFirstTag(from, error);
            }
            try {
                long id = Long.parseUnsignedLong(StringUtils.substringBetween(s, "<#", ">"));
                IChannel channel = command.guild.getChannelByID(id);
                if (channel == null) return replaceFirstTag(from, error);
                channels.add(channel);
            } catch (NumberFormatException e) {
                return replaceFirstTag(from, error);
            }
        }
        if (GuildHandler.canBypass(command) || channels.contains(command.channel.get())) {
            return removeAllTag(from);
        } else {
            return Utility.getChannelMessage(channels);
        }
    }

    @Override
    public String tagName() {
        return "<channelLock>";
    }

    @Override
    protected int argsRequired() {
        return -1;
    }

    @Override
    protected String usage() {
        return "#Channel_1" + splitter + "#Channel_2...";
    }

    @Override
    protected String desc() {
        return "If a Custom Command with this tag is not run in any of the channels specified in the tag arguments it will print an error and close the tag process.";
    }
}
