package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

import java.time.DayOfWeek;

public class NewDailyMessage implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            SplitFirstObject day = new SplitFirstObject(args);
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.getFirstWord().toUpperCase());
            if (day.getRest() != null) {
                IMessage working = Utility.sendMessage("`Working...`", command.channel.get()).get();
                QueueHandler.addToQueue(command, day.getRest(), dayOfWeek, Constants.QUEUE_DAILY);
                Utility.deleteMessage(working);
                return "> Request Sent.";
            } else {
                return Utility.getCommandInfo(this, command);
            }
        } catch (IllegalArgumentException e) {
            return "> Not a valid Day of the week.";
        }
    }

//    public static void checkIsEnabled(boolean enabled) {
//        boolean commandFound = false;
//        for (Command c : Globals.commands) {
//            if (Arrays.equals(c.names(), new NewDailyMessage().names())) {
//                commandFound = true;
//            }
//        }
//        if (!commandFound && enabled) {
//            Globals.commands.add(new NewDailyMessage());
//        } else if (commandFound && !enabled) {
//            for (Command c : Globals.commands) {
//                if (Arrays.equals(c.names(), new NewDailyMessage().names())) {
//                    Globals.commands.remove(c);
//                    return;
//                }
//            }
//        } else {
//            return;
//        }
//    }

    @Override
    public String[] names() {
        return new String[]{"RequestDailyMessage", "RequestDailyMsg", "ReqDailyMsg"};
    }

    @Override
    public String description() {
        return "Allows you to request a new Daily message to be added.\n" +
                "**Available Tags:**\n" +
                "<random>, <randEmote>, <randNum>.";
    }

    @Override
    public String usage() {
        return "[DayOfWeek] [Message]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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