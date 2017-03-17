package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.TimedEvents;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Pattern;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class RemindMe implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        String timeString = args.split(" ")[0];
        try {
            long timeMins = Long.parseLong(timeString);
            if (timeMins < 1){
                return "> Time can not be less than or equal to 0";
            }
            if (timeMins > 1440) {
                return "> Max reminder time is 1 day and your time is to large.";
            }
            String reminderMessage = args.replaceFirst(Pattern.quote(timeString + " "), "");
            if (!TimedEvents.addReminder(command.guildID, command.author.getID(), command.channel.getID(), timeMins, reminderMessage)) {
                return "> Reminder set for " + timeString + " Minute(s) from now.";
            } else {
                return "> You already have a reminder set.";
            }
        } catch (NumberFormatException e) {
            return "> Error Trying to set reminder.\n" +
                    Utility.getCommandInfo(this,command);
        }    }

    @Override
    public String[] names() {
        return new String[]{"RemindMe"};
    }

    @Override
    public String description() {
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
