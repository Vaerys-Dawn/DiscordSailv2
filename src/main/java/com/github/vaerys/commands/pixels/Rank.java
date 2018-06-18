package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;

public class Rank extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        if (args != null && !args.isEmpty()) {
            UserObject userObject = Utility.getUser(command, args, true);
            if (userObject != null) {
                user = userObject;
            } else {
                return "> Could not find user.";
            }
        }
        String error = "> Cannot get rank stats for " + user.displayName + ".";
        if (user.isPrivateProfile(command.guild) && user.longID != command.user.longID) {
            return "> " + user.displayName + " has set their profile to private.";
        } else if (user.isPrivateProfile(command.guild) && user.longID == command.user.longID) {
            return "> You cannot see your ranking as you have set your profile to private.";
        }
        if (user.getProfile(command.guild) == null) {
            return error;
        }
        if (user.getProfile(command.guild).getSettings().contains(UserSetting.HIT_LEVEL_FLOOR)) {
            if (user.get() != command.user.get()) {
                return "> " + user.displayName + " has decayed to the level floor, they will need to level up again to see your rank.";
            } else {
                return "> You have decayed to the level floor, you will need to level up again to see your rank.";
            }
        }
        //grab a copy of the list
        ArrayList<ProfileObject> users = (ArrayList<ProfileObject>) command.guild.users.getProfiles().clone();
        //sort profiles by Xp in ascending order (lowest Xp to highest XP).
        Utility.sortUserObjects(users, true);
        //test to see if said user actually has rank stats.
        if (PixelHandler.rank(command.guild.users, command.guild.get(), user.longID) == -1) {
            return error;
        }
        //build the Array of stats
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == user.longID) {
                ArrayList<ProfileObject> ranks = new ArrayList();
                ArrayList<String> response = new ArrayList();

                int addedTop = 0;
                int addedBottom = 0;
                int posTop = 0;
                int posBottom = 0;
                //add profiles above
                while (addedTop < 3 && i + posTop < users.size()) {
                    if (user.longID != users.get(i + posTop).getUserID() && PixelHandler.rank(command.guild.users, command.guild.get(), users.get(i + posTop).getUserID()) != -1) {
                        addedTop++;
                        ranks.add(users.get(i + posTop));
                    }
                    posTop++;
                }
                //add center user
                ranks.add(users.get(i));
                //add user below
                while (addedBottom < 3 && i + posBottom > 0) {
                    if (user.longID != users.get(i + posBottom).getUserID() && PixelHandler.rank(command.guild.users, command.guild.get(), users.get(i + posBottom).getUserID()) != -1) {
                        addedBottom++;
                        ranks.add(users.get(i + posBottom));
                    }
                    posBottom--;
                }
                //sort ranked profiles
                Utility.sortUserObjects(ranks, false);
                //format rank stats
                for (ProfileObject r : ranks) {
                    IUser ranked = command.guild.getUserByID(r.getUserID());
                    String rankPos = "**" + PixelHandler.rank(command.guild.users, command.guild.get(), r.getUserID()) + "** - ";
                    String toFormat = ranked.getDisplayName(command.guild.get())
                            + "\n " + indent + "`Level: " + r.getCurrentLevel() + ", Pixels: " + NumberFormat.getInstance().format(r.getXP()) + "`";
                    if (r.getUserID() == user.longID) {
                        response.add(rankPos + spacer + "**" + toFormat + "**");
                    } else {
                        response.add(rankPos + toFormat);
                    }
                }
                XEmbedBuilder builder = new XEmbedBuilder(command);
                builder.withTitle("Rank stats for: " + user.displayName);
                builder.withDesc(Utility.listFormatter(response, false));
                RequestHandler.sendEmbedMessage("", builder, command.channel.get());
                return null;
            }
        }
        return error;
    }

    @Override
    protected String[] names() {
        return new String[]{"Rank"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you some information about your rank";
    }

    @Override
    protected String usage() {
        return "(@User)";
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
