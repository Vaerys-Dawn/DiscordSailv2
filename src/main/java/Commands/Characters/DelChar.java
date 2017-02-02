package Commands.Characters;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelChar implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        boolean bypass = false;
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, command.author, command.guild)) {
            bypass = true;
        }
        return command.characters.delChar(args.split(" ")[0], command.author, command.guild, bypass);
    }

    @Override
    public String[] names() {
        return new String[]{"DelChar"};
    }

    @Override
    public String description() {
        return "Deletes a Character.";
    }

    @Override
    public String usage() {
        return "[Character Name]";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
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
