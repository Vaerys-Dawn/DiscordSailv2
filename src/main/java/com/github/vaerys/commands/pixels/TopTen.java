package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;

public class TopTen extends Command {

    protected static final String[] NAMES = new String[]{"TopTen", "Top10"};
    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.PIXELS;
    protected static final String USAGE = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ProfileObject> ranks = new ArrayList<>();
        ArrayList<String> response = new ArrayList<>();

        for (ProfileObject u : command.guild.users.getProfiles()) {
            long rank = XpHandler.rank(command.guild.users, command.guild.get(), u.getUserID());
            if (rank <= 10 && rank != -1) {
                ranks.add(u);
            }
        }

        Utility.sortUserObjects(ranks, false);
        //format rank stats
        for (ProfileObject r : ranks) {
            IUser ranked = command.guild.getUserByID(r.getUserID());
            String rankPos = "**" + XpHandler.rank(command.guild.users, command.guild.get(), r.getUserID()) + "** - ";
            StringBuilder toFormat = new StringBuilder(ranked.getDisplayName(command.guild.get()));
            toFormat.append("\n " + indent + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`");
            if (r.getUserID() == command.user.get().getLongID()) {
                response.add(rankPos + spacer + "**" + toFormat + "**");
            } else {
                response.add(rankPos + toFormat);
            }
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Top Ten Users for the " + command.guild.get().getName() + " Server.");
        builder.withDesc(Utility.listFormatter(response, false));
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a list of the top ten users on the server.";
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
