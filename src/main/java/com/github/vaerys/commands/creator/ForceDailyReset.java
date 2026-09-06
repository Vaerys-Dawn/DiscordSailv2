package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;


public class ForceDailyReset extends Command {


    @Override
    public String execute(String args, CommandObject command) {
        Message message = command.guildChannel.sendMessage("> Attempting to do daily reset...");
        TimerHandler.dailyReset(false);
        message.delete().complete();
        return "\\> Daily reset complete.";
    }

    @Override
    protected String[] names() {
        return new String[]{"ForceDailyReset"};
    }

    @Override
    public String description(CommandObject command) {
        return "forces sail to run daily reset";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
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
        return true;
    }

    @Override
    protected void init() {

    }
}
