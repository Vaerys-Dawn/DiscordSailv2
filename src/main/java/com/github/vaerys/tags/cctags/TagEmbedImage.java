package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.Permission;

import java.net.MalformedURLException;
import java.net.URL;

public class TagEmbedImage extends TagObject {

    public TagEmbedImage(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        String imageURL = getContents(from);
        try {
            new URL(imageURL);
            boolean hasPerm = GuildHandler.testForPerms(command, command.guildChannel.get(), Permission.MESSAGE_EMBED_LINKS);
            String replaceWith = hasPerm ? "" : "<" + imageURL + ">";
            from = replaceAllTag(from, replaceWith);
            from = get(TagRemoveMentions.class).handleTag(from, command, args);
            if (hasPerm) {
                Utility.sendImageFromURL(from, imageURL, command.guildChannel.get(), true);
            } else {
                command.guildChannel.queueMessage(from);
            }
        } catch (MalformedURLException e) {
            from = replaceFirstTag(from, imageURL);
            return get(TagRemoveMentions.class).handleTag(from, command, args);
        }
        return "";
    }

    @Override
    public String tagName() {
        return "<embedImage>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "ImageURL";
    }

    @Override
    public String desc() {
        return "Sends the imageURL as a file.";
    }
}
