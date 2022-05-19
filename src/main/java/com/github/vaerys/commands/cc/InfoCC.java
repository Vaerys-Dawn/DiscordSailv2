package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.EmptyUserObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;


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
        UserObject createdBy;
        try {
            Member member = command.guild.get().retrieveMemberById(customCommand.getUserID()).complete();
            createdBy = new UserObject(member, command.guild);
        }catch (ErrorResponseException e) {
            createdBy = new EmptyUserObject(command.guild);
        }
        builder.append("Creator: **@").append(createdBy.username).append("**\n");
        builder.append("Time Run: **").append(customCommand.getTimesRun()).append("**\n");
        builder.append("Is Locked: **").append(customCommand.isLocked()).append("**\n");
        builder.append("Is ShitPost: **").append(customCommand.isShitPost()).append("**");
        embedBuilder.addField(title, builder.toString(), false);
        embedBuilder.queue(command);
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
        // does nothing
    }
}
