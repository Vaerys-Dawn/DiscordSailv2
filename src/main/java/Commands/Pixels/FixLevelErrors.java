package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.Permissions;

public class FixLevelErrors implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (UserTypeObject u : command.guildUsers.getUsers()) {
            u.setCurrentLevel(XpHandler.xpToLevel(u.getXP()));
        }
        return "> Levels have been adjusted.";
    }

    @Override
    public String[] names() {
        return new String[]{"FixLevelErrors"};
    }

    @Override
    public String description() {
        return "Fixes errors where levels weren't set properly.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
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