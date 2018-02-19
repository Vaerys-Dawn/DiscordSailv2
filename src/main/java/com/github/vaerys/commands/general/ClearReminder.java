package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ClearReminder extends Command {
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

    protected static final String[] NAMES = new String[]{"ClearReminder"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Removes the most recent reminder you set.";
    }

    protected static final String USAGE = null;
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

    protected static final boolean REQUIRES_ARGS = false;
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
