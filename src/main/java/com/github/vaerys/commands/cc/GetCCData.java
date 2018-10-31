package com.github.vaerys.commands.cc;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCCData extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        CCommandObject cc = command.guild.customCommands.getCommand(args);

        if (cc == null) return "> Custom command " + args + " could not be found.";

        StringBuilder content = new StringBuilder("Command Name: \"" + cc.getName() + "\"");
        IUser createdBy = command.guild.getUserByID(cc.getUserID());
        if (createdBy == null) createdBy = command.client.get().fetchUser(cc.getUserID());
        if (createdBy == null) content.append("\nCreated by: \"null\"");
        else {
            content.append("\nCreated by: \"" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "\"");
        }
        content.append("\nTimes run: \"" + cc.getTimesRun() + "\"");
        content.append("\nContents: \"" + cc.getContents(false) + "\"");
        String fileName = String.format("%s.txt",cc.getName());
        RequestHandler.sendFile("> Here is the raw data for Custom Command: **" + cc.getName() + "**", content.toString(), fileName, command.channel.get()).get();
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
