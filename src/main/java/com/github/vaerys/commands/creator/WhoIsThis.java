package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.creator.directmessages.WhoWasThat;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class WhoIsThis extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return get(WhoWasThat.class).execute(args, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"WhoIsThis"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives global info about a globalUser";
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
    protected Permission[] perms() {
        return new Permission[0];
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
