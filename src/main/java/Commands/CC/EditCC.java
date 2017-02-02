package Commands.CC;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EditCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject getName = new SplitFirstObject(args);
        if (getName.getRest() == null) {
            return Utility.getCommandInfo(this,command);
        }
        SplitFirstObject getMode = new SplitFirstObject(getName.getRest());
        return command.customCommands.editCC(getName.getFirstWord(), command.author, command.guild, getMode.getFirstWord(), getMode.getRest());

    }

    @Override
    public String[] names() {
        return new String[]{"EditCC"};
    }

    @Override
    public String description() {
        return "Allows you to edit a custom command.\n" +
                "Modes: Replace, Append, toEmbed,DelCall\n" +
                "Mode is optional, defaults to replace.";
    }

    @Override
    public String usage() {
        return "[Command Name] (Mode) [New Contents]";
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
