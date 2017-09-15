package com.github.vaerys.tags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.TagInterface;

public class tagArgs implements TagInterface {
    @Override
    public String execute(String from, CommandObject command, String args) {
        return from.replace(tagName(), args);
    }

    @Override
    public int priority() {
        return 0;
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
        return null;
    }
}
