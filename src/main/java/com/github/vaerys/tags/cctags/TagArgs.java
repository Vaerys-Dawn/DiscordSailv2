package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;

public class TagArgs extends TagObject {

    public TagArgs(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        if (!from.contains("<dontSanitize>")) {
            args = Utility.prepArgs(args);
        }
        return replaceAllTag(from, args);
    }

    @Override
    public String tagName() {
        return "<args>";
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
        return "Replaces itself with any args given. append <dontSanitise> to the command to stop args from being sanitised.";
    }


    @Override
    public String handleTag(String from, CommandObject command, String args) {
        return execute(from, command, args);
    }
}
