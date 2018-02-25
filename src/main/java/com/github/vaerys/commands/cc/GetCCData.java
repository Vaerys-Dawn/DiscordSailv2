package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCCData extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(args)) {
                StringBuilder content = new StringBuilder("Command Name: \"" + c.getName() + "\"");
                IUser createdBy = command.guild.getUserByID(c.getUserID());
                if (createdBy == null) createdBy = command.client.get().fetchUser(c.getUserID());
                if (createdBy == null) content.append("\nCreated by: \"null\"");
                else
                    content.append("\nCreated by: \"" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "\"");
                content.append("\nTimes run: \"" + c.getTimesRun() + "\"");
                content.append("\nContents: \"" + c.getContents(false) + "\"");
                String filePath = Constants.DIRECTORY_TEMP + command.message.longID + ".txt";
                FileHandler.writeToFile(filePath, content.toString(), false);
                RequestHandler.sendFile("> Here is the raw data for Custom Command: **" + c.getName() + "**", new File(filePath), command.channel.get());
                try {
                    Thread.sleep(4000);
                    new File(filePath).delete();
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
                return "";
            }
        }
        return "> Custom command " + args + " could not be found.";
//        return command.customCommands.sendCCasJSON(command.channelSID, args);
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
