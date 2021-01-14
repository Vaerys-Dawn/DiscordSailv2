package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCCData extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        CCommandObject cc = command.guild.customCommands.getCommand(args);

        if (cc == null) return "\\> Custom command " + args + " could not be found.";

        StringBuilder content = new StringBuilder("Command Name: \"" + cc.getName() + "\"");
        User createdBy = command.guild.getUserByID(cc.getUserID()).getUser();
        if (createdBy == null) createdBy = command.client.getUserByID(cc.getUserID());
        if (createdBy == null) content.append("\nCreated by: \"null\"");
        else {
            content.append("\nCreated by: \"" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "\"");
        }
        content.append("\nTimes run: \"" + cc.getTimesRun() + "\"");
        content.append("\nContents: \"" + cc.getContents(false) + "\"");
        String fileName = String.format("%s.txt",cc.getName());
        command.guildChannel.queueFile("\\> Here is the raw data for Custom Command: **" + cc.getName() + "**", content.toString().getBytes(), fileName);
        return "";
    }

    @Override
    protected String[] names() {
        return new String[]{"GetCCdata"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a Json File with all of the Custom command's data.";
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
