package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
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
    public String[] names() {
        return new String[]{"SetPinLimit"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for editing the pin limit for the art pinning module.";
    }

    @Override
    public String usage() {
        return "[Pin Limit]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}