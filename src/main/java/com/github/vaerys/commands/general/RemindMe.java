package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.EventHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class RemindMe implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        String timeString = args.split(" ")[0];
        try {
            long timeMins = Long.parseLong(timeString);
            if (timeMins < 1) {
                return "> Time can not be less than or equal to 0";
            }
            if (timeMins > 60 * 24 * 365) {
                return "> What are you doing... that reminder is set for over a year from now... you cant do that.";
            }
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            now = now.plusMinutes(timeMins);
            StringBuilder builder = new StringBuilder(args);
            builder.delete(0, (timeMins + "").length());
            if (EventHandler.addReminder(command.user.longID, command.channel.longID, now.toEpochSecond(), builder.toString())) {
                return "> Reminder set for " + timeString + " Minute(s) from now.";
            } else {
                return "> You already have a reminder set.";
            }
        } catch (NumberFormatException e) {
            return "> Error Trying to set reminder.\n" +
                    Utility.getCommandInfo(this, command);
        }
    }

    @Override
    public String[] names() {
        return new String[]{"RemindMe", "Reminder"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets a Reminder for yourself, Limit 1 per user.";
    }

    @Override
    public String usage() {
        return "[Time Minutes] [Reminder Message]";
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
