package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.modules.ModuleRoles;
import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRegion;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetGuildInfo implements Command {


    private XEmbedBuilder resetEmbed(XEmbedBuilder builder, IChannel channel, CommandObject command, int extraLength) {
        if ((builder.getTotalVisibleCharacters() + extraLength) > 2000 ||
                builder.getFieldCount() + 1 > EmbedBuilder.FIELD_COUNT_LIMIT) {
            Utility.sendEmbedMessage("", builder, channel).get();
            builder = new XEmbedBuilder();
            builder.withColor(command.client.color);
        }
        return builder;
    }

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder serverInfo = new XEmbedBuilder();
        XEmbedBuilder toggles = new XEmbedBuilder();
        XEmbedBuilder channels = new XEmbedBuilder();

        boolean isGuildStats = isSubtype(command, "GuildStats");

        IChannel channel = command.user.get().getOrCreatePMChannel();
        boolean hasManageServer = Utility.testForPerms(command, Permissions.MANAGE_SERVER);


        serverInfo.withColor(command.client.color);
        serverInfo.withThumbnail(command.guild.get().getIconURL());
        serverInfo.withTitle(command.guild.get().getName());
        serverInfo.withFooterText("Creation Date");
        serverInfo.withTimestamp(command.guild.get().getCreationDate());

        StringBuilder serverStats = new StringBuilder();
        UserObject owner = new UserObject(command.guild.get().getOwner(), command.guild);
        serverStats.append("**Guild ID:** " + command.guild.longID);
        serverStats.append("\n**Guild Owner:** @" + owner.username);
        IRegion region = command.guild.get().getRegion();
        if (region != null) {
            serverStats.append("\n**Region:** ");
            if (command.guild.get().getRegion().isVIPOnly()) {
                serverStats.append("*VIP* ");
            }
            serverStats.append(command.guild.get().getRegion().getName());
        }
        serverStats.append("\n**Total Users:** " + command.guild.get().getUsers().size());
        serverStats.append("\n**Total Channels:** " + command.guild.get().getChannels().size());
        serverStats.append("\n**Total Roles:** " + command.guild.get().getRoles().size());
        serverStats.append("\n**Command Prefix:** " + command.guild.config.getPrefixCommand());
        if (command.guild.config.moduleCC)
            serverStats.append("\n**Custom Command Prefix:** " + command.guild.config.getPrefixCC());
        if (command.guild.config.rateLimiting)
            serverStats.append("\n**Guild Rate Limit:**" + command.guild.config.messageLimit + "/10s");

        if (hasManageServer && !isGuildStats) {
            serverStats.append("\n\n**[ADMIN STATS]**");
            if (command.guild.config.maxMentions)
                serverStats.append("\n**Max Mentions:**" + command.guild.config.maxMentionLimit);
            if (command.guild.config.denyInvites) {
                serverStats.append("\n**Trusted Roles:** ");
                List<String> trustedRoles = new ArrayList<>();
                for (Long id : command.guild.config.getTrustedRoleIDs()) {
                    IRole role = command.guild.get().getRoleByID(id);
                    if (role != null) {
                        trustedRoles.add(role.getName());
                    }
                }
                serverStats.append(Utility.listFormatter(trustedRoles, true));
            }
        }

        serverInfo.withDescription(serverStats.toString());

        if (isGuildStats) {
            Utility.sendEmbedMessage("", serverInfo, command.channel.get()).get();
            return null;
        } else {
            Utility.sendEmbedMessage("", serverInfo, channel).get();
        }

        if (hasManageServer) {
            toggles.withColor(command.client.color);
            List<String> modules = new ArrayList<>();
            List<String> settings = new ArrayList<>();
            for (GuildToggle t : command.guild.toggles) {
                String formatted = "**" + t.name() + "** - " + t.get(command.guild.config) + indent + spacer;
                if (t.isModule()) {
                    modules.add(formatted);
                } else {
                    settings.add(formatted);
                }
            }
            toggles.appendField("**[TOGGLES]**", Utility.listFormatter(settings, false), true);
            toggles.appendField("**[MODULES]**", Utility.listFormatter(modules, false), true);
            Utility.sendEmbedMessage("", toggles, channel).get();
        }


        if (Utility.testForPerms(command, Permissions.MANAGE_CHANNELS)) {
            channels.withColor(command.client.color);

            List<ChannelSetting> channelSettings = new ArrayList<>(command.guild.channelSettings);
            channelSettings.sort(Comparator.comparing(ChannelSetting::type));
            channelSettings.sort((o1, o2) -> Boolean.compare(o1.isSetting(), o2.isSetting()));

            for (ChannelSetting s : channelSettings) {
                List<String> channelList = new ArrayList<>();
                for (long id : s.getIDs(command.guild.config)) {
                    IChannel ch = command.guild.get().getChannelByID(id);
                    if (ch != null) {
                        channelList.add("#" + ch.getName() + "");
                    }
                }
                if (channelList.size() != 0) {
                    String content = Utility.listFormatter(channelList, true);
                    channels = resetEmbed(channels, channel, command, s.type().length() + content.length());
                    channels.appendField(s.type(), content, true);
                }
            }

            Utility.sendEmbedMessage("", channels, channel);
        }


        XEmbedBuilder moduleStats = new XEmbedBuilder();
        moduleStats.withColor(command.client.color);
        List<GuildToggle> guildmodules = new ArrayList(command.guild.toggles);
        GuildToggle roleModule = null;
        for (GuildToggle t : guildmodules) {
            if (t.name().equalsIgnoreCase(new ModuleRoles().name())) {
                roleModule = t;
            }
        }
        int index = guildmodules.indexOf(roleModule);
        guildmodules.remove(index);
        guildmodules.add(0, roleModule);
        for (GuildToggle toggle : guildmodules) {
            if (toggle.isModule() && toggle.get(command.guild.config)) {
                String stats = toggle.stats(command);
                if (stats != null) {
                    //checks to make sure the field can be added.

                    String[] splitStats = stats.split("\n");
                    List<String> toSend = new ArrayList<>();
                    StringBuilder builder = new StringBuilder();
                    for (String s : splitStats) {
                        if (builder.length() + s.length() + 1 > EmbedBuilder.FIELD_CONTENT_LIMIT / 4) {
                            toSend.add(builder.toString());
                            builder = new StringBuilder();
                        }
                        if (s.startsWith("<split>")) {
                            s = s.replace("<split>", "");
                            toSend.add(builder.toString());
                            builder = new StringBuilder();
                        }
                        builder.append(s + "\n");
                    }
                    if (builder.length() != 0) {
                        toSend.add(builder.toString());
                    }
                    String title = toggle.name().toUpperCase() + " STATS";
                    if (toSend.size() != 0) {
                        for (int i = 0; i < toSend.size(); i++) {
                            moduleStats = resetEmbed(moduleStats, channel, command, title.length() + toSend.get(i).length());
                            if (i + 1 < toSend.size()) {
                                if (toSend.get(i).length() + toSend.get(i + 1).length() < EmbedBuilder.FIELD_CONTENT_LIMIT / 4) {
                                    moduleStats.appendField(title, toSend.get(i) + "\n\n" + toSend.get(i + 1), true);
                                    i++;
                                } else {
                                    moduleStats.appendField(title, toSend.get(i), true);
                                }
                            } else {
                                moduleStats.appendField(title, toSend.get(i), true);
                            }
                        }
                    } else {
                        moduleStats = resetEmbed(moduleStats, channel, command, title.length() + stats.length());
                        moduleStats.appendField(title, stats, true);
                    }
                }
            }
        }
        Utility.sendEmbedMessage("", moduleStats, channel);


        return "> Info sent to Dms.";


//        guild = command.guild.get();
//        author = command.user.get();
//        config = command.guild.config;
//        String guildName = guild.getName();
//        LocalDateTime creationDate = guild.getCreationDate();
//        IUser guildOwner = guild.getOwner();
//        IRegion region = guild.getRegion();
//        List<IRole> roles = guild.getRoles();
//        StringBuilder builder = new StringBuilder();
//        ArrayList<String> cosmeticRoleStats = new ArrayList<>();
//        ArrayList<String> modifierRoleStats = new ArrayList<>();
//        ArrayList<String> trustedRoleStats = new ArrayList<>();
//        int totalCosmetic = 0;
//        int totalModified = 0;
//        int totalTrusted = 0;
//        boolean manageRoles = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild) || author.getLongID() == Globals.creatorID;
//        boolean manageServer = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_SERVER}, author, guild) || author.getLongID() == Globals.creatorID;
//        boolean manageChannels = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_CHANNELS}, author, guild) || author.getLongID() == Globals.creatorID;
//
//        Utility.sendMessage("> Info will be sent to you via Direct Message.", command.channel.get());
//        Utility.sendFileURL("", guild.getIconURL(), command.client.get().getOrCreatePMChannel(command.user.get()), false);
//        builder.append("***[" + guildName.toUpperCase() + "]***");
//        builder.append("\n\n> Guild ID : **" + guild.getLongID());
//        builder.append("**\n> Creation Date : **" + creationDate.getYear() + " " + creationDate.getMonth() + " " + creationDate.getDayOfMonth() + " - " + creationDate.getHour() + ":" + creationDate.getMinute());
//        builder.append("**\n> Guild Owner : **@" + guildOwner.getName() + "#" + guildOwner.getDiscriminator());
//        builder.append("**\n> Command Prefix: **" + config.getPrefixCommand());
//        builder.append("**\n> Custom Command Prefix: **" + config.getPrefixCC() + "**");
//        if (region != null) {
//            builder.append("\n> Region : **" + region.getName() + "**");
//        }
//        builder.append("\n> Total Members: **" + guild.getUsers().size() + "**");
//        if (manageServer) {
//            ArrayList<String> toggles = new ArrayList<>();
//            ArrayList<String> modules = new ArrayList<>();
//            for (GuildToggle t : command.guild.toggles) {
//                String formatted = "\n> " + t.name() + " = **" + t.get(command.guild.config) + "**";
//                if (t.isModule()) {
//                    modules.add(formatted);
//                } else {
//                    toggles.add(formatted);
//                }
//            }
//            Collections.sort(toggles);
//            Collections.sort(modules);
//            builder.append("\n\n***[ADMIN INFO]***");
//            builder.append("\n\n**GUILD TOGGLES**");
//            toggles.forEach(builder::append);
//            builder.append("\n\n**GUILD MODULES**");
//            modules.forEach(builder::append);
//            builder.append("\n\n**GUILD DATA**");
//            builder.append("\n> Max Mentions: **" + config.maxMentionLimit + "**");
//            builder.append("\n> Rate Limit: **" + config.messageLimit + "/10s**");
//        }
//        // TODO: 09/04/2017 add this back in, make it display channels better.
////        if (manageChannels) {
////            builder.append("\n\n***[CHANNELS]***");
////            for (ChannelTypeObject c : config.getChannels()) {
////                builder.append("\n> " + c.getType() + " = **#" + guild.getChannelByID(c.getLongID()).getName() + "**");
////            }
////        }
//
//        builder.append("\n\n***[ROLES]***");
//        ArrayList<RoleStatsObject> statsObjects = new ArrayList<>();
//        for (IRole r : roles) {
//            if (!r.isEveryoneRole()) {
//                statsObjects.add(new RoleStatsObject(r, config, guild.getUsersByRole(r).size()));
//            }
//        }
//        for (RoleStatsObject rso : statsObjects) {
//            StringBuilder formatted = new StringBuilder();
//            formatted.append("\n> **" + rso.getRoleName() + "**");
//            if (manageRoles) {
//                formatted.append(" Colour : \"**" + rso.getColour() + "**\",");
//            }
//            formatted.append(" Total Users: \"**" + rso.getTotalUsers() + "**\"");
//            if (rso.isCosmetic()) {
//                cosmeticRoleStats.add(formatted.toString());
//                totalCosmetic += rso.getTotalUsers();
//            }
//            if (rso.isModifier()) {
//                modifierRoleStats.add(formatted.toString());
//                totalModified += rso.getTotalUsers();
//            }
//            if (rso.isTrusted()) {
//                trustedRoleStats.add(formatted.toString());
//                totalTrusted += rso.getTotalUsers();
//            }
//        }
//        Collections.sort(cosmeticRoleStats);
//        Collections.sort(modifierRoleStats);
//        builder.append("\n\n**TRUSTED ROLES**");
//        for (String s : trustedRoleStats) {
//            if (builder.length() > 1800) {
//                Utility.sendDM(builder.toString(), author.getLongID());
//                builder.delete(0, builder.length());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Utility.sendStack(e);
//                }
//            }
//            builder.append(s);
//        }
//        builder.append("\n > Total profiles : \"**" + totalTrusted + "**\"");
//        builder.append("\n\n**COSMETIC ROLES**");
//        for (String s : cosmeticRoleStats) {
//            if (builder.length() > 1800) {
//                Utility.sendDM(builder.toString(), author.getLongID());
//                builder.delete(0, builder.length());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Utility.sendStack(e);
//                }
//            }
//            builder.append(s);
//        }
//        builder.append("\n > Total profiles : \"**" + totalCosmetic + "**\"");
//        builder.append("\n\n**MODIFIER ROLES**");
//        for (String s : modifierRoleStats) {
//            if (builder.length() > 1800) {
//                Utility.sendDM(builder.toString(), author.getLongID());
//                builder.delete(0, builder.length());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Utility.sendStack(e);
//                }
//
//            }
//            builder.append(s);
//        }
//        builder.append("\n > Total profiles : \"**" + totalModified + "**\"");
//        builder.append("\n\n------{END OF INFO}------");
//        Utility.sendDM(builder.toString(), author.getLongID());
//        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GuildStats", "GuildInfo", "ServerInfo", "GetGuildInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends Information about the server to your Direct Messages.\n" +
                "**SubTypes:**\n" +
                "> " + command.guild.config.getPrefixCommand() + "GuildStats - `Posts a Short description of the server to the current channel.`";
    }

    @Override
    public String usage() {
        return null;
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
