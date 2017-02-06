package Commands.CC;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.CCommandObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class InfoCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (CCommandObject c : command.customCommands.getCommandList()) {
            if (c.getName().equalsIgnoreCase(args)) {
                StringBuilder builder = new StringBuilder();
                IUser author = Globals.getClient().getUserByID(c.getUserID());
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withColor(Utility.getUsersColour(command.client.getOurUser(),command.guild));
                String title = "> Here is the information for command: **" + c.getName() + "**\n";
                builder.append("Creator: **@" + author.getName() + "#" + author.getDiscriminator() + "**\n");
                builder.append("Time Run: **" + c.getTimesRun() + "**\n");
                builder.append("Is Locked: **" + c.isLocked() + "**\n");
                builder.append("Is ShitPost: **" + c.isShitPost() + "**");
                embedBuilder.appendField(title,builder.toString(),false);
                Utility.sendEmbededMessage("",embedBuilder.build(),command.channel);
                return null;
            }
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    @Override
    public String[] names() {
        return new String[]{"CCInfo", "InfoCC"};
    }

    @Override
    public String description() {
        return "Gives you a bit of information about a custom command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
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
