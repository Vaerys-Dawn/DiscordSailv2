package com.github.vaerys.commands.cc;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
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

    @Override
    protected String[] names() {
        return new String[]{"CCInfo", "InfoCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you a bit of information about a custom command.";
    }

    @Override
    protected String usage() {
        return "[Command Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CC;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.CC_INFO;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
