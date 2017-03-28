package Commands.CC;

import Commands.CommandObject;
import Handlers.FileHandler;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.CCommandObject;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCCData implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CCommandObject c : command.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(args)) {
                String content = "Command Name: \"" + c.getName() + "\"";
                content += "\nCreated by: \"" + command.client.getUserByID(c.getUserID()).getDisplayName(command.guild) + "\"";
                content += "\nTimes run: \"" + c.getTimesRun() + "\"";
                content += "\nContents: \"" + c.getContents(false) + "\"";
                String filePath = Constants.DIRECTORY_TEMP + command.messageID + ".txt";
                FileHandler.writeToFile(filePath, content);
                Utility.sendFile("> Here is the raw data for Custom Command: **" + c.getName() + "**", new File(filePath), command.channel);
                try {
                    Thread.sleep(4000);
                    new File(filePath).delete();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }
        return "> Custom command " + args + " could not be found.";
//        return command.customCommands.sendCCasJSON(command.channelID, args);
    }

    @Override
    public String[] names() {
        return new String[]{"GetCCdata"};
    }

    @Override
    public String description() {
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
