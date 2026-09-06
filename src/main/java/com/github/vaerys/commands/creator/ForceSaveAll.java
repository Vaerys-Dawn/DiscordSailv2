package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;


public class ForceSaveAll extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Message message = command.guildChannel.sendMessage("> Attempting to save all files");
        Globals.saveFiles(false);
        message.delete().complete();
        return "> Saved all files?";
    }

    @Override
    protected String[] names() {
        return new String[]{"ForceSaveAll"};
    }

    @Override
    public String description(CommandObject command) {
        return "attempts to force the saving of all files";
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
