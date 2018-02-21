package com.github.vaerys.commands.general;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class RemindMe extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        StringHandler contents = new StringHandler(args);
        long timeSecs = Utility.getRepeatTimeValue(contents);
        if (timeSecs == -1) {
            return "> Could not find a valid time value.\n" +
                    Utility.getCommandInfo(this, command);
        }
        if (timeSecs < 30) {
            return "> You can't set a reminder for less than 30 seconds.";
        }
        if (timeSecs > 60 * 24 * 365 * 60) {
            return "> What are you doing... that reminder is set for over a year from now... you cant do that.";
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        now = now.plusSeconds(timeSecs);
        String response = TimerHandler.addReminder(command.user.longID, command.channel.longID, now.toEpochSecond(), contents.toString());
        int maxSlots = Globals.maxReminderSlots;
        if (command.user.isPatron) {
            maxSlots += maxSlots;
        }
        String timeValue = Utility.formatTime(timeSecs, true);
        switch (response) {
            case "MAX":
                return "> You can only have " + maxSlots + " reminders.";
            case "INTERRUPTS":
                return "> Reminders cannot be within 5 minutes of each other.";
            default:
                return "> Reminder set for " + timeValue + " from now.";
        }
    }

    protected static final String[] NAMES = new String[]{"RemindMe", "Reminder"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Sets a Reminder for yourself, Limit 5 per user (10 for patrons).\n" +
                "**Time**:\n" +
                "> [Num]d - `Days`\n" +
                "> [Num]h - `Hours`\n" +
                "> [Num]m - `Minutes`\n" +
                "> [Num]s - `Seconds`\n" +
                "> Defaults to minutes if no suffix is applied.";
    }

    protected static final String USAGE = "[Time...] [Reminder Message]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
