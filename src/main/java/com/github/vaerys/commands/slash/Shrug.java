package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Shrug extends SlashCommand {

    protected static final String[] NAMES = new String[]{"Shrug"};

    @Override
    public String execute(String args, CommandObject command) {
        return "¯\\_(ツ)_/¯";
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public void init() {

    }
}
