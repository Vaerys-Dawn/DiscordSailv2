package com.github.vaerys.commands.characters;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class EditDungeonChar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return "todo";
    }

    @Override
    public String[] names() {
        return new String[]{"EditDungeonChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing of the dungeon stats for the character.";
    }

    @Override
    public String usage() {
        return "[CharID] [mode] [value]";
    }

    @Override
    public SAILType type() {
        return SAILType.CHARACTER;
    }

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.CHARACTER;
    }

    @Override
    public Permission[] perms() {
        return new Permission[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}