package Commands.Admin;

import Commands.CommandObject;
import Handlers.InfoHandler;
import Interfaces.Command;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateInfo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guildConfig.getChannelIDsByType(Command.CHANNEL_INFO) == null) {
            return "> No Info channel set up yet, you need to set one up in order to run this command.\n" + Utility.getCommandInfo(this, command);
        } else {
            if (command.guildConfig.getChannelIDsByType(Command.CHANNEL_INFO).contains(command.channelSID)) {
                new InfoHandler(command.channel, command.guild);
                return null;
            } else {
                return "> Command must be performed in " + command.client.getChannelByID(command.guildConfig.getChannelIDsByType(CHANNEL_INFO).get(0)).mention() + ".";
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateInfo"};
    }

    @Override
    public String description() {
        return "Posts the contents of the Guild's Info.TXT";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return CHANNEL_INFO;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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
