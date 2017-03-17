package Commands.Help;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 22/02/2017.
 */
public class StartUpGuide implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return "https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/S.A.I.L-Startup-Guide";
    }

    @Override
    public String[] names() {
        return new String[]{"StartUpGuide"};
    }

    @Override
    public String description() {
        return "Posts a link to the Bot's Startup Guide on its wiki.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_HELP;
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
        return false;
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
