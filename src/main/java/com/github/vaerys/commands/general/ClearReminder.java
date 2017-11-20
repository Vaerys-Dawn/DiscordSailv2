package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ClearReminder implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ReminderObject object = null;
        for (ReminderObject r : Globals.getGlobalData().getReminders()) {
            if (r.getUserID() == command.user.longID) {
                object = r;
            }
        }
        if (object != null) {
            if (object.isSent()) {
                return "> Cannot clear reminder, reminder about to be sent.";
            }
            Globals.getGlobalData().removeReminder(object);
            return "> Reminder cleared";
        } else {
            return "> You have no reminders set";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"ClearReminder"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes the most recent reminder you set.";
    }

    @Override
    public String usage() {
        return null;
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
        return false;
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
