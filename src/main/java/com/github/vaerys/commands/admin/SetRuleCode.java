package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class SetRuleCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.message.delete();
        if (args.equalsIgnoreCase("remove")) {
            command.guild.config.setRuleCode(null);
            return "> Rule Code removed.";
        } else {
            command.guild.config.setRuleCode(args);
            return "> Rule Code Added.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetRuleCode"};
    }

    @Override
    public String description(CommandObject command) {
        if (command.guild.config.xpGain) {
            return "Adds a Rule code to the bot for users to use to get some pixels and a star on their profile.";
        } else {
            return "Adds a Rule code to the bot for users to use to get a star on their profile.";
        }
    }

    @Override
    public String usage() {
        return "[New Code]/Remove";
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