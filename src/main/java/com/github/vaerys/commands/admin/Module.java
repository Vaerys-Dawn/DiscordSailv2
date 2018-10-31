package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Module extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return new Toggle().getContent(args, command, true);
    }

    @Override
    public String description(CommandObject command) {
        return "Toggles the specified module.";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"Module", "Modules"};
    }

    @Override
    protected String usage() {
        return "(Module)";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }
}
