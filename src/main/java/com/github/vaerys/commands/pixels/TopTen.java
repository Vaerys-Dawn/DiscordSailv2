package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class TopTen extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ProfileObject> ranks = new ArrayList<>();
        ArrayList<String> response = new ArrayList<>();

        List<ProfileObject> profiles = new ArrayList<>(command.guild.users.profiles);
        Utility.sortUserObjects(profiles, false);
        int counter = 0;
        for (ProfileObject p : profiles) {
            if (counter >= 10) break;
            if (!p.showRank(command.guild)) continue;
            ranks.add(p);
            counter++;
        }

        Utility.sortUserObjects(ranks, false);
        //format rank stats
        for (ProfileObject r : ranks) {
            Member ranked = command.guild.getUserByID(r.getUserID());
            String rankPos = "**" + PixelHandler.rank(command.guild.users, command.guild.get(), r.getUserID()) + "** - ";
            StringBuilder toFormat = new StringBuilder(ranked.getNickname());
            toFormat.append("\n " + INDENT + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`");
            if (r.getUserID() == command.user.get().getIdLong()) {
                response.add(rankPos + SPACER + "**" + toFormat + "**");
            } else {
                response.add(rankPos + toFormat);
            }
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle("Top Ten Users for the " + command.guild.get().getName() + " Server.");
        builder.setDescription(Utility.listFormatter(response, false));
        builder.queue(command);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"TopTen", "Top10"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a list of the top ten users on the server.";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.PIXELS;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
