package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 10/05/2017.
 */
public class ToggleTypingStatus extends Command {

    @Override
    public String execute(String args, CommandObject command) {
//        command.channel.getAllToggles().toggleTypingStatus();
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Typing"};
    }

    @Override
    public String description(CommandObject command) {
        return "Toggles the typing status on a channel.";
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
