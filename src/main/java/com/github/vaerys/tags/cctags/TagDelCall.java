package com.github.vaerys.tags.cctags;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

public class TagDelCall extends TagObject {

    public TagDelCall(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        if (GuildHandler.testForPerms(command,Permissions.MANAGE_MESSAGES)) {
            RequestHandler.deleteMessage(command.message.get());
        }
        return removeAllTag(from);
    }

    @Override
    public String tagName() {
        return "<delCall>";
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
        return "Removes the message that called this command.";
    }
}
