package Commands.General;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        return "> Nothing to test at the moment come back later.";
    }

    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description() {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public String dualDescription() {
        return "This is another test.";
    }

    @Override
    public String dualUsage() {
        return "[Blep]";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
    }
}
