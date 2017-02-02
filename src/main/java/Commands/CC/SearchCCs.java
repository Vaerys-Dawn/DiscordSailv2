package Commands.CC;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class SearchCCs implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return command.customCommands.search(args, command.guildConfig, command.channel, command.messageID);
    }

    @Override
    public String[] names() {
        return new String[]{"SearchCCs"};
    }

    @Override
    public String description() {
        return "Allows you to search the custom command list.";
    }

    @Override
    public String usage() {
        return "[Search Params]";
    }

    @Override
    public String type() {
        return TYPE_CC;
    }

    @Override
    public String channel() {
        return null;
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
