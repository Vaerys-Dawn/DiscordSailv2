package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;

public class TopTen implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ProfileObject> ranks = new ArrayList<>();
        ArrayList<String> response = new ArrayList<>();

        for (ProfileObject u : command.guild.users.getProfiles()) {
            long rank = XpHandler.rank(command.guild.users, command.guild.get(), u.getID());
            if (rank <= 10 && rank != -1) {
                ranks.add(u);
            }
        }

        Utility.sortUserObjects(ranks, false);
        //format rank stats
        for (ProfileObject r : ranks) {
            IUser ranked = command.guild.get().getUserByID(r.getID());
            String rankPos = "**" + XpHandler.rank(command.guild.users, command.guild.get(), r.getID()) + "** - ";
            StringBuilder toFormat = new StringBuilder(ranked.getDisplayName(command.guild.get()));
            toFormat.append("\n " + indent + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`");
            if (r.getID().equals(command.user.get().getStringID())) {
                response.add(rankPos + spacer + "**" + toFormat + "**");
            } else {
                response.add(rankPos + toFormat);
            }
        }
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withTitle("Top Ten Users for the " + command.guild.get().getName() + " Server.");
        builder.withDesc(Utility.listFormatter(response, false));
        builder.withColor(Utility.getUsersColour(command.client.bot, command.guild.get()));
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"TopTen","Top10"};
    }

    @Override
    public String description() {
        return "Gives a list of the top ten profiles on the server.";
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