package com.github.vaerys.commands.general;

import com.github.vaerys.commands.creator.DailyMsg;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class LastDailyMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        DailyMessage messageObject = command.guild.config.getLastDailyMessage();
        if (messageObject != null) {
            new DailyMsg().getInfo(messageObject, command).queue(command);
            return null;
        } else return "\\> It appears that there have been no daily messages stored.";
    }

    @Override
    protected String[] names() {
        return new String[]{"LastDailyMessage", "LastDailyMsg"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the information of the last Daily message that was sent to this server.";
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
