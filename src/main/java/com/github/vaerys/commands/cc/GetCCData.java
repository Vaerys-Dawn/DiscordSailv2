package com.github.vaerys.commands.cc;

import java.io.File;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

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
                else content.append("\nCreated by: \"" + createdBy.getName() + "#" + createdBy.getDiscriminator() + "\"");
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

    protected static final String[] NAMES = new String[]{"GetCCdata"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a Json File with all of the Custom command's data.";
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
