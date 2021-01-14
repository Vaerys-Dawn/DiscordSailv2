package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Client;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateAvatar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Client.handleAvatars();
        return "\\> Avatar Updated.";
    }

    @Override
    protected String[] names() {
        return new String[]{"UpdateAvatar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Update's the bot's Avatar.\n" + ownerOnly;
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
    public void init() {

    }
}
