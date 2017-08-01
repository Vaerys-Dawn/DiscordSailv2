package Commands.Admin;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.GuildToggle;
import Main.Utility;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Module implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return new Toggle().getContent(args, command, true, this);
    }

    @Override
    public String[] names() {
        return new String[]{"Module"};
    }

    @Override
    public String description() {
        return "Allows for the toggle of certain commands.";
    }

    @Override
    public String usage() {
        return "(Module Type)";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
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
