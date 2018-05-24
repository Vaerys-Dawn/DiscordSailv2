package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.time.Instant;
import java.util.List;

public class GetReminders extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<ReminderObject> reminders = Globals.getGlobalData().getRemindersUser(command.user.longID);

        if (reminders.size() == 0) return "> You have no reminders set.";

        XEmbedBuilder embed = new XEmbedBuilder(command);
        int i = 1;
        for (ReminderObject r : reminders) {
            long timeSecs = r.getExecuteTime() - Instant.now().getEpochSecond();
            String timeFormatted = Utility.formatTime(timeSecs, true);

            IChannel channel = command.client.get().getChannelByID(r.getChannelID());
            if (channel == null) {
                // try to verify it's a DM channel instead.
                channel = command.user.getDmChannel();
                if ( channel == null || (channel.getLongID() != r.getChannelID()) ) continue;
            }

            String mention = "your Direct Messages";
            String guildName = "";
            if (!channel.isPrivate()) {
                // not a DM channel.
                mention = channel.mention();
                IGuild guild = channel.getGuild();
                guildName = (guild.getLongID() == command.guild.longID) ? "" : " (" + guild.getName() + ")";
            }

            embed.appendDesc( String.format("\n`%d.` **%s** in %s%s.", i, timeFormatted, mention, guildName) );
            i++;
        }
        i--;   // oh of course
        embed.withTitle( String.format("> You have %d Reminder%s:", i, (i==1) ? "":"s" ) );
        embed.send(command);
        return null;
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
