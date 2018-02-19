package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Lenny extends SlashCommand {

    @Override
    public String execute(String args, CommandObject command) {
        return "( ͡° ͜ʖ ͡°)";
    }

    protected static final String[] NAMES = new String[]{"Lenny"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public void init() {

    }
}
