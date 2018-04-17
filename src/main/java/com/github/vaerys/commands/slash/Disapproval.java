package com.github.vaerys.commands.slash;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Disapproval extends SlashCommand {

    @Override
    public String execute(String args, CommandObject command) {
        return "ಠ_ಠ";
    }

    @Override
    protected String[] names() {
        return new String[]{"Disapprove"};
    }
}
