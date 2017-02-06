package Commands.Help;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Report implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        return report(args, command, false);
    }

    public static String report(String args, CommandObject command, boolean isSilent) {
        String reported;
        String channelID = command.guildConfig.getChannelTypeID(CHANNEL_ADMIN);
        SplitFirstObject user = new SplitFirstObject(args);
        IUser reportedUser = command.client.getUserByID(user.getFirstWord());

        if (reportedUser != null) {
            reported = reportedUser.mention();
        } else if (command.message.getMentions().size() > 0) {
            reported = command.message.getMentions().get(0).mention();
        } else {
            return "> User Report could not be sent as you did not specify a user.";
        }

        if (channelID != null) {
            StringBuilder builder = new StringBuilder();
            if (command.guildConfig.getRoleToMention().getRoleID() != null) {
                builder.append(command.guild.getRoleByID(command.guildConfig.getRoleToMention().getRoleID()).mention() + "\n");
            }
            if (isSilent) {
                builder.append("**User Report - Silent**\n");
            } else {
                builder.append("**User Report**\n");
            }
            user.editRestReplace(user.getRest(),Utility.convertMentionToText(user.getRest()));
            builder.append("Reporter: " + command.author.mention() + "\nReported: " + reported + "\nReason: `" + user.getRest() + "`");
            builder.append("\n" + command.channel.mention());
            Utility.sendMessage(builder.toString(), command.guild.getChannelByID(channelID));
            return "> User Report sent.";
        } else {
            return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Report"};
    }

    @Override
    public String description() {
        return "Can be used to send a user report to the server staff.";
    }

    @Override
    public String usage() {
        return "[@user]/[User ID] [Reason]";
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
