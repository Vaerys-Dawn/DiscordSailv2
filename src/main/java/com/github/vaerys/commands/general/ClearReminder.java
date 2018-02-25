package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ClearReminder extends Command {
    protected static final String[] NAMES = new String[]{"ClearReminder"};
    protected static final String USAGE = null;
    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

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
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Removes the most recent reminder you set.";
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
