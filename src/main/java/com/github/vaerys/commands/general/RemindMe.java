package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class RemindMe implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        String timeString = args.split(" ")[0];
        try {
            long timeSecs = 0;
            List<String> list = new ArrayList<>();
            for (String s : args.split(" ")) {
                if (Pattern.compile("[0-9]*(s|m|h|d)").matcher(s).matches()) {
                    list.add(s);
                } else {
                    break;
                }
            }
            if (list.size() == 0) {
                timeSecs = Long.parseLong(timeString) * 60;
                list.add(timeString);
            } else {
                for (String s : list) {
                    timeSecs += Utility.textToSeconds(s);
                }
            }
            String message = StringUtils.replaceOnce(args, String.join(" ", list), "");
            if (message.startsWith(" ")) {
                message = StringUtils.replaceOnce(message, " ", "");
            }
            if (timeSecs < 30) {
                return "> you can't set a reminder for less than 30 seconds.";
            }
            if (timeSecs > 60 * 24 * 365 * 60) {
                return "> What are you doing... that reminder is set for over a year from now... you cant do that.";
            }

//            return "> " + timeSecs + " " + message;
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            now = now.plusSeconds(timeSecs);
            String response = TimerHandler.addReminder(command.user.longID, command.channel.longID, now.toEpochSecond(), message);
            int maxSlots = Globals.maxReminderSlots;
            if (command.user.isPatron) {
                maxSlots += maxSlots;
            }
            String timeValue = Utility.formatTime(timeSecs,true);
            switch (response) {
                case "MAX":
                    return "> You can only have " + maxSlots + " reminders.";
                case "INTERRUPTS":
                    return "> Reminders cannot be within 5 minutes of each other.";
                default:
                    return "> Reminder set for " + timeValue + " from now.";
            }
        } catch (NumberFormatException e) {
            return "> Error Trying to set reminder. Invalid time.\n" +
                    Utility.getCommandInfo(this, command);
        }
    }

    @Override
    public String[] names() {
        return new String[]{"RemindMe", "Reminder"};
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

    @Override
    public String usage() {
        return "[Time...] [Reminder Message]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
        return true;
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
