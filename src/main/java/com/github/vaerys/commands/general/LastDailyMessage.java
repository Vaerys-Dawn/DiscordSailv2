package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.DailyMsg;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

public class LastDailyMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        DailyMessage messageObject = command.guild.config.getLastDailyMessage();
        if (messageObject != null) {
            RequestHandler.sendEmbedMessage("", new DailyMsg().getInfo(messageObject, command), command.channel.get());
            return null;
        } else return "> It appears that there have been no daily messages stored.";
    }

    protected static final String[] NAMES = new String[]{"LastDailyMessage", "LastDailyMsg"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the information of the last Daily message that was sent to this server.";
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