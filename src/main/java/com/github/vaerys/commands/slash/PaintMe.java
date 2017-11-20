package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.SlashCommand;

public class PaintMe implements SlashCommand {
    @Override
    public String execute(String args, CommandObject command) {
        return "∠( ᐛ 」∠)＿";
    }

    @Override
    public String[] names() {
        return new String[]{"PaintMe","PaintMeLike","FrenchGirl"};
    }
}