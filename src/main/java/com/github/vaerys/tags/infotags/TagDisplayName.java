package com.github.vaerys.tags.infotags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.IUser;

public class TagDisplayName extends TagObject {

    public TagDisplayName(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        IUser user = command.guild.getUserByID(Utility.stringLong(contents(from)));
        if (user != null) {
            return replaceFirstTag(from, user.getDisplayName(command.guild.get()));
        } else {
            return replaceFirstTag(from, error);
        }
    }

    @Override
    public String tagName() {
        return "<displayName>";
    }

    @Override
    public int argsRequired() {
        return 1;
    }

    @Override
    public String usage() {
        return "UserID";
    }

    @Override
    public String desc() {
        return "Replaces the tag with the display name of the user.";
    }
}
