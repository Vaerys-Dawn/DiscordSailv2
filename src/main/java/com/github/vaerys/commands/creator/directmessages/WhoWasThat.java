package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.templates.DMCommand;
import com.github.vaerys.utilobjects.XEmbedBuilder;

import java.text.NumberFormat;
import java.util.stream.Collectors;

public class WhoWasThat extends DMCommand {

    @Override
    public String executeDm(String args, DmCommandObject command) {
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
            return "\\> No globalUser found.";
        }
        XEmbedBuilder embed = new XEmbedBuilder(user);
        embed.setAuthor("@" + user.username);
        if (user.isPatron) embed.setAuthor("@" + user.username, Constants.PATREON_ICON_URL);
        embed.setThumbnail(user.get().getAvatarUrl());
        StringBuilder builder = new StringBuilder();
        final double[] totalxp = {0};
        final double[] ccActive = {0};
        final double[] charsActive = {0};
        final double[] serversActive = {0};
        user.getProfiles().forEach(profileObject -> totalxp[0] += profileObject.getXP());
        user.getGuilds().forEach(guildObject -> {
            if (guildObject.config.moduleCC) ccActive[0]++;
            if (guildObject.config.moduleChars) charsActive[0]++;
            if (guildObject.config.moduleServers) serversActive[0]++;
        });
        String mutualGuilds = Utility.listFormatter(user.getGuilds().stream().map(guildObject -> guildObject.get().getName()).collect(Collectors.toList()), true);
        builder.append("**Mutual Guilds:** ");
        if (mutualGuilds.length() > 300) {
            builder.append(user.getGuilds().size());
        } else {
            builder.append(mutualGuilds);
        }
        builder.append("\n**Total Profiles:** " + user.getProfiles().size());
        builder.append("\n**Total Pixels:** " + formatter.format(totalxp[0]) + "    **Average Pixels:** " + formatter.format(totalxp[0] / user.getProfiles().size()));

        builder.append("\n**Total CCs:** " + formatter.format(user.getCustomCommands().size()));
        if (ccActive[0] >= 0 && user.getCustomCommands().size() != 0)
            builder.append("    **Average CCs:** " + formatter.format((double) user.getCustomCommands().size() / ccActive[0]));

        builder.append("\n**Total Chars:** " + formatter.format(user.getCharacters().size()));
        if (charsActive[0] >= 0 && user.getCharacters().size() != 0)
            builder.append("    **Average Chars:** " + formatter.format((double) user.getCharacters().size() / charsActive[0]));

        builder.append("\n**Total Servers:** " + formatter.format(user.getServers().size()));
        if (serversActive[0] >= 0 && user.getServers().size() != 0)
            builder.append("    **Average Servers:** " + formatter.format((double) user.getServers().size() / serversActive[0]));

        builder.append("\n**Total DailyMessages:** " + user.getDailyMessages().size());
        builder.append("\n**Total Reminders:** " + user.getReminders().size());

        embed.setFooter(user.longID + ", Creation Date");
        embed.setTimestamp(user.get().getTimeCreated().toInstant());
        embed.setDescription(builder.toString());
        embed.queue(command.messageChannel);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"WhoWasThat", "Who"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about the most recent Dm globalUser.";
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
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
