package com.github.vaerys.templates;

import java.util.Arrays;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 13/03/2017.
 */
public abstract class SlashCommand extends Command {

    public String description(CommandObject command) {
        return "Returns with " + execute(null, null) + ".";
    }

    public String usage() {
        return null;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.SLASH;
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
    @Override
    public String getCommand(CommandObject command) {
        return "/" + names()[0];
    }

    @Override
    public String getUsage(CommandObject command) {
        return getCommand(command);
    }

    @Override
    public String missingArgs(CommandObject command) {
        return null;
    }

    @Override
    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject object = new SplitFirstObject(args);
        return object.getFirstWord().equalsIgnoreCase(getCommand(command));
    }

    @Override
    public String getArgs(String args, CommandObject command) {
        return null;
    }

    @Override
    public XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(getCommand(command));
        builder.withDescription(description(command));
        if (names().length != 1) {
            builder.appendField("Aliases:", Utility.listFormatter(Arrays.asList(names()),true),true);
        }
        return builder;
    }

    @Override
    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (names().length == 0 || names()[0].isEmpty()) {
            response.append("> NAME IS EMPTY.\n");
            isErrored = true;
        }
        response.append(">> End Error Report <<");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }
}
