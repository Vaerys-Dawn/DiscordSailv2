package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ToggleShitPost implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        return command.customCommands.toggleShitPost(args);
    }

    @Override
    public String[] names() {
        return new String[]{"ToggleShitPost","ToggleShit"};
    }

    @Override
    public String description() {
        return "Toggles the isShitPost tag of a custom command.";
    }

    @Override
    public String usage() {
        return "[CC Name]";
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
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
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
