package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.MessageHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/02/2017.
 */
public class Sudo extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (!command.guild.config.debugMode) return "> Debug mode is disabled. No Sudo for you missy.";
        SplitFirstObject sudo = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, sudo.getFirstWord(), false, false);
        if (user == null) {
            return "> Could not find user.";
        }
        command.setAuthor(user.get());
        if (sudo.getRest() == null) {
            return "> You need to specify some arguments.";
        }
        try {
            new MessageHandler(sudo.getRest(), command, false);
        } catch (Exception e) {
            String errorPos = "";
            for (StackTraceElement s : e.getStackTrace()) {
                if (s.toString().contains("com.github.vaerys")) {
                    errorPos = s.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append("> I caught an Error, Please send this Error message and the message that caused this error " +
                    "to my **Direct Messages** so my developer can look at it and try to solve the issue.\n```\n");
            builder.append(e.getClass().getName());
            builder.append(": " + e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n" + Constants.PREFIX_INDENT + "at " + errorPos);
            }
            builder.append("```");
            RequestHandler.sendMessage(builder.toString(), command.channel.get());
            Utility.sendStack(e);
        }
        return null;
    }

    protected static final String[] NAMES = new String[]{"Sudo"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Runs a command as though you were someone else.\n" + ownerOnly;
    }

    protected static final String USAGE = "[@User] [Command + args]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
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

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
