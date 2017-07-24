package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Utility;
import Objects.UserTypeObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;

public class Rank implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        IUser user = command.author;
        String error = "> Cannot get rank stats for this user.";
        if (command.message.getMentions().size() > 0) {
            user = command.message.getMentions().get(0);
        }
        //grab a copy of the list
        ArrayList<UserTypeObject> users = (ArrayList<UserTypeObject>) command.guildUsers.getUsers().clone();
        //sort users by Xp in ascending order (lowest Xp to highest XP).
        Utility.sortUserObjects(users, true);
        //test to see if said user actually has rank stats.
        if (XpHandler.rank(command.guildUsers, command.guild, user.getStringID()) == -1) {
            return error;
        }
        //build the Array of stats
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getID().equals(user.getStringID())) {
                ArrayList<UserTypeObject> ranks = new ArrayList();
                ArrayList<String> response = new ArrayList();

                int addedTop = 0;
                int addedBottom = 0;
                int posTop = 0;
                int posBottom = 0;
                //add users above
                while (addedTop < 3 && i + posTop < users.size()) {
                    if (!user.getStringID().equals(users.get(i + posTop).getID()) && XpHandler.rank(command.guildUsers, command.guild, users.get(i + posTop).getID()) != -1) {
                        addedTop++;
                        ranks.add(users.get(i + posTop));
                    }
                    posTop++;
                }
                //add center user
                ranks.add(users.get(i));
                //add user below
                while (addedBottom < 3 && i + posBottom > 0) {
                    if (!user.getStringID().equals(users.get(i + posBottom).getID()) && XpHandler.rank(command.guildUsers, command.guild, users.get(i + posBottom).getID()) != -1) {
                        addedBottom++;
                        ranks.add(users.get(i + posBottom));
                    }
                    posBottom--;
                }
                //sort ranked users
                Utility.sortUserObjects(ranks, false);
                //format rank stats
                for (UserTypeObject r : ranks) {
                    IUser ranked = command.guild.getUserByID(r.getID());
                    String rankPos = "**" + XpHandler.rank(command.guildUsers, command.guild, r.getID()) + "** - ";
                    String toFormat = ranked.getDisplayName(command.guild)
                            + "\n " + indent + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`";
                    if (r.getID().equals(user.getStringID())) {
                        response.add(rankPos + spacer + "**" + toFormat + "**");
                    } else {
                        response.add(rankPos + toFormat);
                    }
                }
                XEmbedBuilder builder = new XEmbedBuilder();
                builder.withTitle("Rank stats for: " + user.getDisplayName(command.guild));
                builder.withDesc(Utility.listFormatter(response, false));
                builder.withColor(Utility.getUsersColour(command.botUser, command.guild));
                Utility.sendEmbedMessage("", builder, command.channel);
                return null;
            }
        }
        return error;
    }

    @Override
    public String[] names() {
        return new String[]{"Rank"};
    }

    @Override
    public String description() {
        return "Gives you some information about your rank";
    }

    @Override
    public String usage() {
        return "(@User)";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return CHANNEL_PIXELS;
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