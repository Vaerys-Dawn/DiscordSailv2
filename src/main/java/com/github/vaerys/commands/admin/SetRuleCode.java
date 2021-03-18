package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class SetRuleCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.message.delete();
        if (args.equalsIgnoreCase("remove")) {
            command.guild.config.setRuleCode(null);
            return "\\> Rule Code removed.";
        } else {
            command.guild.config.setRuleCode(args);
            return "\\> Rule Code Added.";
        }
    }

    @Override
    protected String[] names() {
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
    protected String usage() {
        return "[New Code]/Remove";
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
        // does nothing
    }
}
