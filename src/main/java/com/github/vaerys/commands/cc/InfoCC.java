package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class InfoCC extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        CCommandObject customCommand = command.guild.customCommands.getCommand(args);
        if (customCommand == null) {
            return Constants.ERROR_CC_NOT_FOUND;
        }
        StringBuilder builder = new StringBuilder();
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String title = "> Here is the information for command: **" + customCommand.getName() + "**\n";
        IUser createdBy = command.guild.getUserByID(customCommand.getUserID());
        if (createdBy == null) createdBy = command.client.get().fetchUser(customCommand.getUserID());
        if (createdBy == null) builder.append("Creator: **Null**\n");
        else builder.append("Creator: **@" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "**\n");
        builder.append("Time Run: **" + customCommand.getTimesRun() + "**\n");
        builder.append("Is Locked: **" + customCommand.isLocked() + "**\n");
        builder.append("Is ShitPost: **" + customCommand.isShitPost() + "**");
        embedBuilder.appendField(title, builder.toString(), false);
        RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;

    }

    protected static final String[] NAMES = new String[]{"CCInfo", "InfoCC"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you a bit of information about a custom command.";
    }

    protected static final String USAGE = "[Command Name]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CC;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.CC_INFO;
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

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
