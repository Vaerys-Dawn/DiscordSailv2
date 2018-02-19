package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.TimedEvent;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Hello extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String message = "> Hello <user>.";
        TimedEvent event = Globals.getCurrentEvent();
        if (event != null && event.getHelloMessage() != null) message = event.getHelloMessage();
        if (command.user.longID == 153159020528533505L) {
            message = message.replace("<user>", "Mother");
        }
        return message.replace("<user>", command.user.displayName);
    }

    protected static final String[] NAMES = new String[]{"Hello", "Hi", "Greetings"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Says Hello.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
