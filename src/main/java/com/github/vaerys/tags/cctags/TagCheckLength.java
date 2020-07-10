package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.TagObject;

public class TagCheckLength extends TagObject {

    public TagCheckLength(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        if (from.length() > 2000) {
            RequestHandler.sendFile("ERROR: Custom command output exceeds 2000 characters.", from, "Error.txt", command.channel.get());
//            RequestHandler.queueMessage("ERROR: Custom command output exceeds 2000 characters.", command.channel.get());
            return null;
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<checkLength>";
    }

    @Override
    public boolean isPassive() {
        return true;
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
        return "Checks the length of the cc. If the final output is over 2000 chars it sends an error.";
    }

    @Override
    public String handleTag(String from, CommandObject command, String args) {
        return execute(from,command,args);
    }
}
