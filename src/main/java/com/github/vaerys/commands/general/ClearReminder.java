package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.objects.userlevel.ReminderObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ClearReminder extends Command {

    @Override
    public String executeDm(String args, DmCommandObject command) {
        return doClearReminders(args, command.globalUser);
    }

    @Override
    public String execute(String args, CommandObject command) {
        return doClearReminders(args, command.user);
    }

    private String doClearReminders(String args, GlobalUserObject user) {
        ReminderObject object = null;
        for (ReminderObject r : Globals.getGlobalData().getReminders()) {
            if (r.getUserID() == user.longID) {
                object = r;
            }
        }
        if (object != null) {
            if (object.isSent()) {
                return "\\> Cannot clear reminder, reminder about to be sent.";
            }
            Globals.getGlobalData().removeReminder(object);
            return "\\> Reminder cleared";
        } else {
            return "\\> You have no reminders set";
        }
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"ClearReminder"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes the most recent reminder you set.";
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
