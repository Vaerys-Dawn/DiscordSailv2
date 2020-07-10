package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.botlevel.ToggleStatsObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 25/02/2017.
 */
public class GetGlobalStats extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ToggleStatsObject> toggleStats = new ArrayList<>();
        ArrayList<String> outToggles = new ArrayList<>();
        ArrayList<String> outModules = new ArrayList<>();
//        ArrayList<ChannelStatsObject> channelStats = new ArrayList<>();
        for (GuildToggle g : ToggleList.getAllToggles()) {
            toggleStats.add(new ToggleStatsObject(g.name().toString(), g.isModule()));
        }
//        for (ChannelSetting c : Globals.getChannelSettings()) {
//            channelStats.add(new ChannelStatsObject(c.type(), c.isSetting()));
//        }
        long totalXpUsers = 0;
        long totalXP = 0;
        for (GuildObject g : Globals.getGuilds()) {
            CommandObject object = command.setGuild(g.get());
            for (ToggleStatsObject s : toggleStats) {
                for (GuildToggle t : object.guild.getToggles()) {
                    if (t.name().toString().equalsIgnoreCase(s.getToggle())) {
                        if (t.isModule()) {
                            s.addOne();
                        } else if (t.enabled(object.guild.config) != t.getDefault()) {
                            s.addOne();
                        }
                    }
                }
            }
            //re enable to find channel stats
//            for (ChannelStatsObject s : channelStats) {
//                for (TextChannel c : command.guild.getChannels()) {
//                    ArrayList<String> channelIDs = command.guildConfig.getChannelIDsByType(s.getNames());
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
        command.user.queueDm(output.toString());
        return "\\> Data sent to Your DMs";

//        int totalGlobalUsers = 0;
//        int totalAvg = 0;
//        for (Guild guild : command.client.getGuilds()) {
//            StringBuilder builder = new StringBuilder();
//            long totalUsers = 0;
//            long totalMessage = 0;
//            long totalMessageAvg = 0;
//            long topGuild = 0;
//            long bottomGuild = 0;
//            String topUser = null;
//            String bottomUser = null;
//            GuildContentObject content = Globals.getGuildContent(guild.getIdLong());
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
//            builder.append("**Guild: " + guild.getNames() + "**");
//            IUser topIUser = command.client.getUserByID(topUser);
//            IUser bottomIUser = command.client.getUserByID(bottomUser);
//            if (topIUser != null && bottomIUser != null) {
//                builder.append("\nTop User = @" + command.client.getUserByID(topUser).getNames() + "#" + command.client.getUserByID(topUser).getDiscriminator());
//                builder.append(" With Total Messages " + topGuild);
//                builder.append("\nBottom User = @" + command.client.getUserByID(bottomUser).getNames() + "#" + command.client.getUserByID(bottomUser).getDiscriminator());
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
    protected String[] names() {
        return new String[]{"GetGlobalStats"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends the owner captured message counters.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
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
