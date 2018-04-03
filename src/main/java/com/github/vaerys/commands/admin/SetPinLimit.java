package com.github.vaerys.commands.admin;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class SetPinLimit extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int newMax = Integer.parseInt(args);
            if (newMax < 1 || newMax > 50) {
                return "> Number must be between 1 and 50 inclusive.";
            }
            command.guild.config.setPinLimit(newMax);
            return "> Pin Limit updated.";
        } catch (NumberFormatException e) {
            return "> Not a valid number.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetPinLimit"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing the pin limit for the art pinning module.";
    }

    @Override
    protected String usage() {
        return "[Pin Limit]";
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
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
