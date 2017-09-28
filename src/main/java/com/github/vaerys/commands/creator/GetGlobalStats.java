package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.ToggleStatsObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 25/02/2017.
 */
public class GetGlobalStats implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ToggleStatsObject> toggleStats = new ArrayList<>();
        ArrayList<String> outToggles = new ArrayList<>();
        ArrayList<String> outModules = new ArrayList<>();
//        ArrayList<ChannelStatsObject> channelStats = new ArrayList<>();
        for (GuildToggle g : Globals.getGuildGuildToggles()) {
            toggleStats.add(new ToggleStatsObject(g.name(), g.isModule()));
        }
//        for (ChannelSetting c : Globals.getChannelSettings()) {
//            channelStats.add(new ChannelStatsObject(c.type(), c.isSetting()));
//        }
        long totalXpUsers = 0;
        long totalXP = 0;
        for (GuildObject g : Globals.getGuilds()) {
            CommandObject object = command.setGuild(g.get());
            for (ToggleStatsObject s : toggleStats) {
                for (GuildToggle t : object.guild.toggles) {
                    if (t.name().equalsIgnoreCase(s.getToggle())) {
                        if (t.isModule()) {
                            s.addOne();
                        } else if (t.get(object.guild.config) != t.getDefault()) {
                            s.addOne();
                        }
                    }
                }
            }
            //re enable to find channel stats
//            for (ChannelStatsObject s : channelStats) {
//                for (IChannel c : command.guild.getChannels()) {
//                    ArrayList<String> channelIDs = command.guildConfig.getChannelIDsByType(s.getName());
//                    if (channelIDs != null) {
//                        s.addCounts(channelIDs.size());
//                    }
//                }
//            }
            if (command.guild.config.modulePixels) {
                for (ProfileObject o : command.guild.users.getProfiles()) {
                    if (o.getXP() != 0) {
                        totalXP += o.getXP();
                        totalXpUsers++;
                    }
                }
            }
        }
        for (ToggleStatsObject t : toggleStats) {
            String formatted = t.getToggle() + ": " + t.getCount();
            if (t.isModule()) {
                outModules.add(formatted);
            } else {
                outToggles.add(formatted);
            }
        }
        StringBuilder output = new StringBuilder();
        output.append("**[GLOBAL STATS]**");
        output.append("\n\n**[TOGGLE STATS]**\n");
        output.append(Utility.listFormatter(outToggles, false));
        output.append("\n\n**[MODULE STATS]**\n");
        output.append(Utility.listFormatter(outModules, false));
        output.append("\n\n**[PIXEL STATS]**");
        output.append("\nAvg Pixels: " + totalXP / totalXpUsers);
        Utility.sendDM(output.toString(), command.user.longID);
        return "> Data sent to Your DMs";

//        int totalGlobalUsers = 0;
//        int totalAvg = 0;
//        for (IGuild guild : command.client.getGuilds()) {
//            StringBuilder builder = new StringBuilder();
//            long totalUsers = 0;
//            long totalMessage = 0;
//            long totalMessageAvg = 0;
//            long topGuild = 0;
//            long bottomGuild = 0;
//            String topUser = null;
//            String bottomUser = null;
//            GuildContentObject content = Globals.getGuildContent(guild.getLongID());
//            for (UserTypeObject user : content.getGuildUsers().getProfiles()) {
//                if (bottomGuild == 0) {
//                    bottomGuild = user.getXP();
//                    bottomUser = user.getID();
//                } else if (bottomGuild > user.getXP()) {
//                    bottomGuild = user.getXP();
//                    bottomUser = user.getID();
//                }
//                if (topGuild == 0) {
//                    topGuild = user.getXP();
//                    topUser = user.getID();
//                } else if (topGuild < user.getXP()) {
//                    topGuild = user.getXP();
//                    topUser = user.getID();
//                }
//                totalMessage += user.getXP();
//                totalGlobalUsers++;
//                totalUsers++;
//            }
//            builder.append("**Guild: " + guild.getName() + "**");
//            IUser topIUser = command.client.getUserByID(topUser);
//            IUser bottomIUser = command.client.getUserByID(bottomUser);
//            if (topIUser != null && bottomIUser != null) {
//                builder.append("\nTop User = @" + command.client.getUserByID(topUser).getName() + "#" + command.client.getUserByID(topUser).getDiscriminator());
//                builder.append(" With Total Messages " + topGuild);
//                builder.append("\nBottom User = @" + command.client.getUserByID(bottomUser).getName() + "#" + command.client.getUserByID(bottomUser).getDiscriminator());
//                builder.append(" With Total Messages " + bottomGuild);
//                builder.append("\nGuild Avg = " + totalMessageAvg / totalUsers);
//                builder.append("\nTotal Guild Messages = " + totalMessage);
//                builder.append("\nTotal Users = " + totalUsers);
//                Utility.sendDM(builder.toString(), command.authorSID);
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                Utility.sendStack(e);
//            }
//        }
//        StringBuilder builder = new StringBuilder("**Global Stats**");
//        builder.append("\nGlobal profiles = " + totalGlobalUsers);
//        builder.append("\nGlobal Avg = " + totalAvg / totalGlobalUsers);
//        Utility.sendDM(builder.toString(), command.authorSID);
//        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GetGlobalStats"};
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
