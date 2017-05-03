package Commands.Creator;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.GuildContentObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 25/02/2017.
 */
public class GetMessageData implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        int totalGlobalUsers = 0;
        int totalAvg = 0;
        for (IGuild guild : command.client.getGuilds()) {
            String builder = "";
            int totalUsers = 0;
            int totalMessage = 0;
            int totalMessageAvg = 0;
            int topGuild = 0;
            int bottomGuild = 0;
            String topUser = null;
            String bottomUser = null;
            GuildContentObject content = Globals.getGuildContent(guild.getStringID());
            for (UserTypeObject user : content.getGuildUsers().getUsers()) {
                if (bottomGuild == 0) {
                    bottomGuild = user.getXP();
                    bottomUser = user.getID();
                } else if (bottomGuild > user.getXP()) {
                    bottomGuild = user.getXP();
                    bottomUser = user.getID();
                }
                if (topGuild == 0) {
                    topGuild = user.getXP();
                    topUser = user.getID();
                } else if (topGuild < user.getXP()) {
                    topGuild = user.getXP();
                    topUser = user.getID();
                }
                totalMessage += user.getXP();
                totalMessageAvg += user.getXP() / (user.getLevel() + 1);
                totalAvg += user.getXP() / (user.getLevel() + 1);
                totalGlobalUsers++;
                totalUsers++;
            }
            builder += "**Guild: " + guild.getName() + "**";
            IUser topIUser = command.client.getUserByID(topUser);
            IUser bottomIUser = command.client.getUserByID(bottomUser);
            if (topIUser != null && bottomIUser != null) {
                builder += "\nTop User = @" + command.client.getUserByID(topUser).getName() + "#" + command.client.getUserByID(topUser).getDiscriminator();
                builder += " With Total Messages " + topGuild;
                builder += "\nBottom User = @" + command.client.getUserByID(bottomUser).getName() + "#" + command.client.getUserByID(bottomUser).getDiscriminator();
                builder += " With Total Messages " + bottomGuild;
                builder += "\nGuild Avg = " + totalMessageAvg / totalUsers;
                builder += "\nTotal Guild Messages = " + totalMessage;
                builder += "\nTotal Users = " + totalUsers;
                Utility.sendDM(builder, command.authorID);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String builder = "**Global Stats**";
        builder += "\nGlobal users = " + totalGlobalUsers;
        builder += "\nGlobal Avg = " + totalAvg / totalGlobalUsers;
        Utility.sendDM(builder,command.authorID);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GetMessageData"};
    }

    @Override
    public String description() {
        return "Sends the owner captured message counters.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
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
