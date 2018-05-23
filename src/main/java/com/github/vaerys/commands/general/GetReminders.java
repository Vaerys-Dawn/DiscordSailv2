package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class GetReminders extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ReminderObject> reminders = new ArrayList<>();
        for (ReminderObject r : Globals.getGlobalData().getReminders()) {
            if (r.getUserID() == command.user.longID) {
                reminders.add(r);
            }
        }

        if (reminders.size() > 0) {
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            StringBuilder replystr = new StringBuilder("> Here are all of your current Reminders:\n");
            for (int i=0; i < reminders.size(); i++) {
                ReminderObject rem = reminders.get(i);
                replystr.append( "**" + convSec(rem.getExecuteTime() - now.toEpochSecond()) + "**" )
                        .append( " | " )
                        .append( Utility.convertMentionToText(
                                    rem.getMessage().replaceFirst("<@!?"+command.user.longID+"> ","")
                                ))  // remove user mentions and remove the default reminder mention.
                        .append( "\n" );
            }
            return replystr.toString();
        } else {
            return "> You have no reminders set.";
        }
    }

    private static String convSec(long totalSecs) {  // move to Utility
        int secs = ((int) totalSecs) % 60;
        long totalMins = totalSecs / 60;

        int mins = ((int) totalMins) % 60;
        long totalHours = totalMins / 60;

        int hours = ((int) totalHours) % 24;
        int days = ((int) totalHours) / 24;

        // format is 999d, 23h 59m 59s
        // or 23h 59m 59s
        // or 59m 59s
        // and 0m 59s
        return String.format("%s%s%sm %ss",
                (days > 0)  ? (days + "d, ") : "",
                (hours > 0) ? (hours + "h ") : "",
                mins, secs);
    }

    @Override
    protected String[] names() {
        return new String[]{"GetReminders","Reminders"};
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
