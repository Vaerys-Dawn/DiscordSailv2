package com.github.vaerys.commands.slash;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.SlashCommand;
import sx.blah.discord.handle.obj.Permissions;

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