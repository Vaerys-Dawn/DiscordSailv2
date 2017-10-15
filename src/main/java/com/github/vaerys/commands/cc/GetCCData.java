package com.github.vaerys.commands.cc;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCCData implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CCommandObject c : command.guild.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(args)) {
                StringBuilder content = new StringBuilder("Command Name: \"" + c.getName() + "\"");
                IUser createdBy = command.guild.getUserByID(c.getUserID());
                if (createdBy == null) createdBy = command.client.get().fetchUser(c.getUserID());
                if (createdBy == null) content.append("\nCreated by: \"null\"");
                else content.append("\nCreated by: \"" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "\"");
                content.append("\nTimes run: \"" + c.getTimesRun() + "\"");
                content.append("\nContents: \"" + c.getContents(false) + "\"");
                String filePath = Constants.DIRECTORY_TEMP + command.message.longID + ".txt";
                FileHandler.writeToFile(filePath, content.toString(), false);
                Utility.sendFile("> Here is the raw data for Custom Command: **" + c.getName() + "**", new File(filePath), command.channel.get());
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
    public String[] names() {
        return new String[]{"GetCCdata"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a Json File with all of the Custom command's data.";
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
        return CHANNEL_BOT_COMMANDS;
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
