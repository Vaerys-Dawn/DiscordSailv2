package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.SlashCommand;

public class PaintMe extends SlashCommand {
    @Override
    public String execute(String args, CommandObject command) {
        return "∠( ᐛ 」∠)＿";
    }

    protected static final String[] NAMES = new String[]{"PaintMe","PaintMeLike","FrenchGirl"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public void init() {

    }
}