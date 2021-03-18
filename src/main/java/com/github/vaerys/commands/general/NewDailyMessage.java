package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.botlevel.AutoBlocker;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class NewDailyMessage extends Command {

    private static final List<AutoBlocker> lastUsers = new ArrayList<>(5);

    @Override
    public String execute(String args, CommandObject command) {
        try {
            SplitFirstObject day = new SplitFirstObject(args);
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.getFirstWord().toUpperCase());
            addUser(command);
            if (command.user.isBlockedFromDms()) {
                return "\\> You have been blocked. You cannot send daily message requests.";
            }
            if (day.getRest() != null) {
                Message working = command.guildChannel.sendMessage("`Working...`");
                QueueHandler.addToQueue(command, day.getRest(), dayOfWeek, Constants.QUEUE_DAILY);
                working.delete().complete();
                return "\\> Request Sent.";
            } else {
                return missingArgs(command);
            }
        } catch (IllegalArgumentException e) {
            return "\\> Not a valid Day of the week.";
        }
    }

    private static void addUser(CommandObject command) {
        boolean foundUser = false;
        for (AutoBlocker a : lastUsers) {
            if (a.getUserID() == command.user.longID) {
                foundUser = true;
                a.addCount(command.message.getTimestamp());
            }
        }
        if (!foundUser) lastUsers.add(new AutoBlocker(command));
        try {
            while (lastUsers.size() > 5) {
                lastUsers.remove(0);
            }
        } catch (ConcurrentModificationException e) {
            // do nothing
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"RequestDailyMessage", "RequestDailyMsg", "ReqDailyMsg", "NewDailyMsg", "NewDailyMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to request a new Daily message to be added.\n" +
                "**Tags:** " + Utility.listFormatter(TagList.getNames(TagType.DAILY), true) +
                "\n\n**Themes:**\n" +
                "Monday - Cat\n" +
                "Tuesday - Portal\n" +
                "Wednesday - Avali\n" +
                "Thursday - Joke\n" +
                "Friday - Ruin\n" +
                "Saturday - Anything\n" +
                "Sunday - Anything\n";
    }

    @Override
    protected String usage() {
        return "[DayOfWeek] [Message]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
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
