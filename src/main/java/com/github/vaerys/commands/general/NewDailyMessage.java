package com.github.vaerys.commands.general;

import java.time.DayOfWeek;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

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

    protected static final String[] NAMES = new String[]{"RequestDailyMessage", "RequestDailyMsg", "ReqDailyMsg","NewDailyMsg","NewDailyMessage"};
    @Override
    protected String[] names() {
        return NAMES;
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

    protected static final String USAGE = "[DayOfWeek] [Message]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.BOT_COMMANDS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
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