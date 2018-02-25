package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.directmessages.WhoWasThat;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class WhoIsThis extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return new WhoWasThat().execute(args, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"WhoIsThis"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives global info about a user";
    }

    @Override
    protected String usage() {
        return "(UserID)";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
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
