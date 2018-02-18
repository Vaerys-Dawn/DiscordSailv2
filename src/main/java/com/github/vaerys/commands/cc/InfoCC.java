package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
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
    public String[] names() {
        return new String[]{"CCInfo", "InfoCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you a bit of information about a custom command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return CHANNEL_CC_INFO;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
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

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
