package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class Patreon extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return "> https://www.patreon.com/DawnFelstar";
    }

    @Override
    protected String[] names() {
        return new String[]{"Patreon"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the dev's patreon link.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
