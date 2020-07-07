package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
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
        String title = "\\> Here is the information for command: **" + customCommand.getName() + "**\n";
        UserObject createdBy = UserObject.getNewUserObject(customCommand.getUserID(), command.guild);
        builder.append("Creator: **@" + createdBy.username + "**\n");
        builder.append("Time Run: **" + customCommand.getTimesRun() + "**\n");
        builder.append("Is Locked: **" + customCommand.isLocked() + "**\n");
        builder.append("Is ShitPost: **" + customCommand.isShitPost() + "**");
        embedBuilder.addField(title, builder.toString(), false);
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
    protected Permission[] perms() {
        return new Permission[0];
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
