package com.github.vaerys.commands.setup;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class SetupQuit extends Command {

    protected static final SAILType COMMAND_TYPE = SAILType.SETUP;
    protected static final String[] NAMES = new String[]{"quit", "stop"};
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;


    @Override
    public String execute(String args, CommandObject command) {
        SetupHandler.setSetupStage(command, (short)SetupHandler.SETUP_UNSET);
        return null;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Stops setup and resets any progress on the setup.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() { return COMMAND_TYPE; }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
