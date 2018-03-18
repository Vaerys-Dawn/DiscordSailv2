package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

import java.net.MalformedURLException;
import java.net.URL;

public class TagEmbedImage extends TagObject {

    public TagEmbedImage(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        String imageURL = contents(from);
        try {
            new URL(imageURL);
            boolean hasPerm = GuildHandler.testForPerms(command, command.channel.get(), Permissions.EMBED_LINKS);
            String replaceWith = hasPerm ? "" : "<" + imageURL + ">";
            from = replaceAllTag(from, replaceWith);
            from = new TagRemoveMentions(0).handleTag(from, command, args);
            if (hasPerm) {
                RequestHandler.sendFileURL(from, imageURL, command.channel.get(), true);
            } else {
                RequestHandler.sendMessage(from, command.channel.get());
            }
        } catch (MalformedURLException e) {
            from = replaceFirstTag(from, imageURL);
            return new TagRemoveMentions(0).handleTag(from, command, args);
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
