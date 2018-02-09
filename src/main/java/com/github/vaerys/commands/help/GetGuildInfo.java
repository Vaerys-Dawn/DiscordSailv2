package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.modules.ModuleRoles;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
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
public class GetGuildInfo extends Command {


    private XEmbedBuilder resetEmbed(XEmbedBuilder builder, IChannel channel, CommandObject command, int extraLength) {
        if ((builder.getTotalVisibleCharacters() + extraLength) > 2000 ||
                builder.getFieldCount() + 1 > EmbedBuilder.FIELD_COUNT_LIMIT) {
            RequestHandler.sendEmbedMessage("", builder, channel).get();
            builder = new XEmbedBuilder(command);
        }
        return builder;
    }

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder serverInfo = new XEmbedBuilder(command);
        XEmbedBuilder toggles = new XEmbedBuilder(command);
        XEmbedBuilder channels = new XEmbedBuilder(command);

        boolean isGuildStats = isSubtype(command, "GuildStats");

        IChannel channel = command.user.get().getOrCreatePMChannel();
        boolean hasManageServer = Utility.testForPerms(command, Permissions.MANAGE_SERVER);


        //todo change this to the proper impl when api allows it.


        boolean isVIP = command.guild.get().getRegion().isVIPOnly();
        if (isVIP) {
            serverInfo.withAuthorIcon("https://i.imgur.com/m0jqzBn.png");
        }

        serverInfo.withThumbnail(command.guild.get().getIconURL());
        serverInfo.withAuthorName(command.guild.get().getName());
        serverInfo.withFooterText("Creation Date");
        serverInfo.withTimestamp(command.guild.get().getCreationDate());

        StringBuilder serverStats = new StringBuilder();
        UserObject owner = new UserObject(command.guild.getOwner(), command.guild);
        serverStats.append("**Guild ID:** " + command.guild.longID);
        serverStats.append("\n**Guild Owner:** @" + owner.username);
        IRegion region = command.guild.get().getRegion();
        if (region != null) {
            serverStats.append("\n**Region:** ");
            serverStats.append(command.guild.get().getRegion().getName());
        }
        serverStats.append("\n**Total Users:** " + command.guild.getUsers().size());
        serverStats.append("\n**Total Channels:** " + command.guild.get().getChannels().size());
        serverStats.append("\n**Total Voice Channels:** " + command.guild.get().getVoiceChannels().size());
        serverStats.append("\n**Total Roles:** " + command.guild.get().getRoles().size());
        serverStats.append("\n**Command Prefix:** " + command.guild.config.getPrefixCommand());
        if (command.guild.config.moduleCC)
            serverStats.append("\n**Custom Command Prefix:** " + command.guild.config.getPrefixCC());
        if (command.guild.config.rateLimiting)
            serverStats.append("\n**Guild Rate Limit:** " + command.guild.config.messageLimit + "/10s");

        if (hasManageServer && !isGuildStats) {
            StringBuilder adminBuilder = new StringBuilder();
            if (command.guild.config.maxMentions)
                adminBuilder.append("\n**Max Mentions:** " + command.guild.config.maxMentionLimit);
            if (command.guild.config.muteRepeatOffenders && command.guild.config.getMutedRoleID() != -1)
                adminBuilder.append("\n**Messages Until AutoMute:** " + (command.guild.config.messageLimit - 3));
            if (command.guild.config.denyInvites) {
                adminBuilder.append("\n**Trusted Roles:** ");
                List<String> trustedRoles = new ArrayList<>();
                for (Long id : command.guild.config.getTrustedRoleIDs()) {
                    IRole role = command.guild.getRoleByID(id);
                    if (role != null) {
                        trustedRoles.add(role.getName());
                    }
                }
                adminBuilder.append(Utility.listFormatter(trustedRoles, true));
            }
            if (adminBuilder.length() != 0) {
                serverStats.append("\n\n**[ADMIN STATS]**" + adminBuilder.toString());
            }
        }

        serverInfo.withDescription(serverStats.toString());

        if (isGuildStats) {
            RequestHandler.sendEmbedMessage("", serverInfo, command.channel.get()).get();
            return null;
        } else {
            RequestHandler.sendEmbedMessage("", serverInfo, channel).get();
        }

        if (hasManageServer) {
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
            toggles.appendField("**TOGGLES**", Utility.listFormatter(settings, false), true);
            toggles.appendField("**MODULES**", Utility.listFormatter(modules, false), true);
            RequestHandler.sendEmbedMessage("", toggles, channel).get();
        }


        if (Utility.testForPerms(command, Permissions.MANAGE_CHANNELS)) {

            List<ChannelSetting> channelSettings = new ArrayList<>(command.guild.channelSettings);
            channelSettings.sort(Comparator.comparing(ChannelSetting::name));
            channelSettings.sort((o1, o2) -> Boolean.compare(o1.isSetting(), o2.isSetting()));

            for (ChannelSetting s : channelSettings) {
                List<String> channelList = new ArrayList<>();
                for (long id : s.getIDs(command.guild.config)) {
                    IChannel ch = command.guild.getChannelByID(id);
                    if (ch != null) {
                        channelList.add("#" + ch.getName() + "");
                    }
                }
                if (channelList.size() != 0) {
                    String content = Utility.listFormatter(channelList, true);
                    channels = resetEmbed(channels, channel, command, s.name().length() + content.length());
                    channels.appendField(s.name(), content, true);
                }
            }
            channels.withTitle("CHANNEL STATS");
            RequestHandler.sendEmbedMessage("", channels, channel).get();
        }

        //module builders.
        XEmbedBuilder moduleStats = new XEmbedBuilder(command);
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
        RequestHandler.sendEmbedMessage("", moduleStats, channel).get();

        return "> Info sent to Dms.";
    }

    @Override
    public String[] names() {
        return new String[]{"GuildInfo", "GuildStats", "ServerInfo", "GetGuildInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends Information about the server to your Direct Messages.\n" +
                "**SubCommands:**\n" +
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
    public void init() {

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
