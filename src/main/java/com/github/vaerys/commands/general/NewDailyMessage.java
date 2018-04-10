package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

import java.time.DayOfWeek;

public class NewDailyMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            SplitFirstObject day = new SplitFirstObject(args);
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.getFirstWord().toUpperCase());
            if (day.getRest() != null) {
                IMessage working = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();
                QueueHandler.addToQueue(command, day.getRest(), dayOfWeek, Constants.QUEUE_DAILY);
                RequestHandler.deleteMessage(working);
                return "> Request Sent.";
            } else {
                return Utility.getCommandInfo(this, command);
            }
        } catch (IllegalArgumentException e) {
            return "> Not a valid Day of the week.";
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
