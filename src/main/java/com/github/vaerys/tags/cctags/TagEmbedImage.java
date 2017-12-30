package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

import java.net.MalformedURLException;
import java.net.URL;

public class TagEmbedImage extends TagObject {

    public TagEmbedImage(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        String imageURL = contents(from);
        try {
            URL url = new URL(imageURL);
            if (Utility.testForPerms(command, command.channel.get(), Permissions.EMBED_LINKS)) {
                from = removeAllTag(from);
                RequestHandler.sendFileURL(from, imageURL, command.channel.get(), true);
            } else {
                from = replaceAllTag(from, "<" + imageURL + ">");
                RequestHandler.sendMessage(from, command.channel.get());
            }
        } catch (MalformedURLException e) {
            return replaceFirstTag(from, imageURL);
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
