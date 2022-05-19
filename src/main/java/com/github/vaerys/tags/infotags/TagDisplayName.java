package com.github.vaerys.tags.infotags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.entities.Member;

public class TagDisplayName extends TagObject {

    public TagDisplayName(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        Member user = command.guild.getUserByID(Utility.stringLong(getContents(from)));
        if (user != null) {
            return replaceFirstTag(from, user.getEffectiveName());
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
