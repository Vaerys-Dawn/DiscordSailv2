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

public class TopTen implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<UserTypeObject> ranks = new ArrayList<>();
        ArrayList<String> response = new ArrayList<>();

        for (UserTypeObject u : command.guildUsers.getUsers()) {
            long rank = XpHandler.rank(command.guildUsers, command.guild, u.getID());
            if (rank <= 10 && rank != -1) {
                ranks.add(u);
            }
        }

        Utility.sortUserObjects(ranks, false);
        //format rank stats
        for (UserTypeObject r : ranks) {
            IUser ranked = command.guild.getUserByID(r.getID());
            String rankPos = "**" + XpHandler.rank(command.guildUsers, command.guild, r.getID()) + "** - ";
            StringBuilder toFormat = new StringBuilder(ranked.getDisplayName(command.guild));
            toFormat.append("\n " + indent + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`");
            if (r.getID().equals(command.author.getStringID())) {
                response.add(rankPos + spacer + "**" + toFormat + "**");
            } else {
                response.add(rankPos + toFormat);
            }
        }
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withTitle("Top Ten Users for the " + command.guild.getName() + " Server.");
        builder.withDesc(Utility.listFormatter(response, false));
        builder.withColor(Utility.getUsersColour(command.botUser, command.guild));
        Utility.sendEmbedMessage("", builder, command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"TopTen"};
    }

    @Override
    public String description() {
        return "Gives a list of the top ten users on the server.";
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