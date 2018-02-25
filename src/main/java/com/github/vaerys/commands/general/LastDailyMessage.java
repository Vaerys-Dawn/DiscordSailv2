package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.DailyMsg;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class LastDailyMessage extends Command {

    protected static final String[] NAMES = new String[]{"LastDailyMessage", "LastDailyMsg"};
    protected static final String USAGE = null;
    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        DailyMessage messageObject = command.guild.config.getLastDailyMessage();
        if (messageObject != null) {
            RequestHandler.sendEmbedMessage("", new DailyMsg().getInfo(messageObject, command), command.channel.get());
            return null;
        } else return "> It appears that there have been no daily messages stored.";
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the information of the last Daily message that was sent to this server.";
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
