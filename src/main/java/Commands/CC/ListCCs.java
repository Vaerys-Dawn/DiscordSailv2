package Commands.CC;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Globals;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class ListCCs implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IMessage message = command.message;
        CustomCommands customCommands = command.customCommands;
        IGuild guild = command.guild;
        GuildConfig guildConfig = command.guildConfig;
        if (message.getMentions().size() > 0) {
            return customCommands.getUserCommands(message.getMentions().get(0).getID(), guild, guildConfig);
        }
        IUser mentionedUser = Globals.getClient().getUserByID(args);
        if (mentionedUser != null) {
            return customCommands.getUserCommands(mentionedUser.getID(), guild, guildConfig);
        }
        try {
            int page;
            if (args == null || args.isEmpty()) {
                page = 1;
            } else {
                page = Integer.parseInt(args.split(" ")[0]);
            }
            return customCommands.listCommands(page, guildConfig);
        } catch (NumberFormatException e) {
            return "> what are you doing, why are you trying to search for the " + args + " page... \n" +
                    Constants.PREFIX_INDENT + "pretty sure you cant do that...";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"CClist", "ListCCs"};
    }

    @Override
    public String description() {
        return "Generates a list of custom commands based on parameters.";
    }

    @Override
    public String usage() {
        return "[Page Number]/@User/[UserID]";
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
