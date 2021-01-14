package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class RemindMe extends Command {


    @Override
    public String execute(String args, CommandObject command) {
        return reminder(args, command.guildChannel, command.user, missingArgs(command));
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        return reminder(args, command.messageChannel, command.globalUser, missingArgsDm());
    }

    public String reminder(String args, ChannelObject channel, GlobalUserObject user, String missingArgs) {
        StringHandler contents = new StringHandler(args);
        long timeSecs = Utility.getRepeatTimeValue(contents);
        if (timeSecs == -1) {
            return "\\> Could not find a valid time value.\n" + missingArgs;
        }
        if (timeSecs < 30) {
            return "\\> You can't set a reminder for less than 30 seconds.";
        }
        if (timeSecs > 60 * 24 * 365 * 60) {
            return "\\> What are you doing... that reminder is set for over a year from now... you cant do that.";
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        now = now.plusSeconds(timeSecs);
        String response = TimerHandler.addReminder(user.longID, channel.longID, now.toEpochSecond(), contents.toString());
        int maxSlots = Globals.maxReminderSlots;
        if (user.isPatron) {
            maxSlots += maxSlots;
        }
        String timeValue = Utility.formatTime(timeSecs, true);
        switch (response) {
            case "MAX":
                return "\\> You can only have " + maxSlots + " reminders.";
            case "INTERRUPTS":
                return "\\> Reminders cannot be within 5 minutes of each other.";
            default:
                return "\\> Reminder set for " + timeValue + " from now.";
        }
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"RemindMe", "Reminder"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets a Reminder for yourself, Limit 5 per globalUser (10 for patrons).\n" +
                "**Time**:\n" +
                "\\> [Num]d - `Days`\n" +
                "\\> [Num]h - `Hours`\n" +
                "\\> [Num]m - `Minutes`\n" +
                "\\> [Num]s - `Seconds`\n" +
                "\\> Defaults to minutes if no suffix is applied.";
    }

    @Override
    protected String usage() {
        return "[Time...] [Reminder Message]";
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
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
