package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.userlevel.ReminderObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.util.List;

public class GetReminders extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return doGetReminders(command.user, command.client, command.guildChannel, command.guild.longID, new XEmbedBuilder(command));
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        return doGetReminders(command.globalUser, command.client, command.messageChannel, -1, new XEmbedBuilder());
    }

    private String doGetReminders(GlobalUserObject user, ClientObject client, ChannelObject channel, long guildID, XEmbedBuilder embed) {
        List<ReminderObject> reminders = Globals.getGlobalData().getRemindersUser(user.longID);

        if (reminders.size() == 0) return "\\> You have no reminders set.";

        int i = 1;
        for (ReminderObject r : reminders) {
            long timeSecs = r.getExecuteTime() - Instant.now().getEpochSecond();
            String timeFormatted = Utility.formatTime(timeSecs, true);

            MessageChannel reminderChannel = client.get().getTextChannelById(r.getChannelID());
            if (reminderChannel == null) {
                // try to verify it's a DM messageChannel instead.
                reminderChannel = user.getDmChannel();
                if ( channel == null ) continue;
            }

            String mention = "your Direct Messages";
            String guildName = "";
            if (reminderChannel.getType() == ChannelType.TEXT) {
                // not a DM messageChannel.
                TextChannel textChannel = client.get().getTextChannelById(reminderChannel.getIdLong());
                mention = textChannel.getAsMention();
                Guild guild = textChannel.getGuild();
                guildName = (guild.getIdLong() == guildID) ? "" : " (" + guild.getName() + ")";
            }

            embed.setDescription( String.format("\n`%d.` **%s** in %s%s.", i, timeFormatted, mention, guildName) );
            i++;
        }
        i--;   // oh of course
        embed.setTitle( String.format("\\> You have %d Reminder%s:", i, (i==1) ? "":"s" ) );
        channel.queueMessage(embed.build());
        return null;
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"GetReminders", "Reminders", "ListReminders"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets a list of your current Reminders.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
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
