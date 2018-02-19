package com.github.vaerys.commands.creator.directmessages;

import java.text.NumberFormat;
import java.util.stream.Collectors;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

public class WhoWasThat extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        GlobalUserObject user = null;
        if (args.length() != 0) {
            try {
                long userID = Long.parseUnsignedLong(args);
                user = new GlobalUserObject(userID);
            } catch (NumberFormatException e) {

            }
        }
        NumberFormat formatter = NumberFormat.getInstance();
        if (user == null) {
            user = new GlobalUserObject(Globals.lastDmUserID);
        }
        if (user.get() == null) {
            return "> No user found.";
        }
        XEmbedBuilder embed = new XEmbedBuilder(command);
        embed.withAuthorName("@" + user.username);
        if (user.isPatron) embed.withAuthorIcon(Constants.PATREON_ICON_URL);
        embed.withThumbnail(user.get().getAvatarURL());
        StringBuilder builder = new StringBuilder();
        final double[] totalxp = {0};
        final double[] ccActive = {0};
        final double[] charsActive = {0};
        final double[] serversActive = {0};
        user.profiles.forEach(profileObject -> totalxp[0] += profileObject.getXP());
        user.guilds.forEach(guildObject -> {
            if (guildObject.config.moduleCC) ccActive[0]++;
            if (guildObject.config.moduleChars) charsActive[0]++;
            if (guildObject.config.moduleServers) serversActive[0]++;
        });
        String mutualGuilds = Utility.listFormatter(user.guilds.stream().map(guildObject -> guildObject.get().getName()).collect(Collectors.toList()), true);
        builder.append("**Mutual Guilds:** ");
        if (mutualGuilds.length() > 300) {
            builder.append(user.guilds.size());
        } else {
            builder.append(mutualGuilds);
        }
        builder.append("\n**Total Profiles:** " + user.profiles.size());
        builder.append("\n**Total Pixels:** " + formatter.format(totalxp[0]) + "    **Average Pixels:** " + formatter.format(totalxp[0] / user.profiles.size()));

        builder.append("\n**Total CCs:** " + formatter.format(user.customCommands.size()));
        if (ccActive[0] >= 0 && user.customCommands.size() != 0)
            builder.append("    **Average CCs:** " + formatter.format((double) user.customCommands.size() / ccActive[0]));

        builder.append("\n**Total Chars:** " + formatter.format(user.characters.size()));
        if (charsActive[0] >= 0 && user.characters.size() != 0)
            builder.append("    **Average Chars:** " + formatter.format((double) user.characters.size() / charsActive[0]));

        builder.append("\n**Total Servers:** " + formatter.format(user.servers.size()));
        if (serversActive[0] >= 0 && user.servers.size() != 0)
            builder.append("    **Average Servers:** " + formatter.format((double) user.servers.size() / serversActive[0]));

        builder.append("\n**Total DailyMessages:** " + user.dailyMessages.size());
        builder.append("\n**Total Reminders:** " + user.reminders.size());

        embed.withFooterText(user.longID + ", Creation Date");
        embed.withTimestamp(user.get().getCreationDate());
        embed.withDesc(builder.toString());
        embed.send(command.channel);
        return null;
    }

    protected static final String[] NAMES = new String[]{"WhoWasThat", "Who"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about the most recent Dm user.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.FROM_DM;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}