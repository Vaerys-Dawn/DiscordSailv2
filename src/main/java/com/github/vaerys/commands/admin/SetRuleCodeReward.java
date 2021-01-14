package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public class SetRuleCodeReward extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        Role role = GuildHandler.getRoleFromName(args, command.guild.get());
        if (role == null) return "\\> Not a valid Role name.";
        if (command.guild.config.ruleCodeRewardID != -1) {
            if (args.equalsIgnoreCase("remove")) {
                command.guild.config.ruleCodeRewardID = -1;
                return "\\> RuleCode Reward removed.";
            }
            return "\\> You already have a RuleCode Reward set.";
        }
        command.guild.config.ruleCodeRewardID = role.getIdLong();
        return "\\> **" + role.getName() + "** Is now the RuleCode Reward.";
    }

    @Override
    public String[] names() {
        return new String[]{"SetRuleCodeReward"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows admins to set up a role that will automatically be assigned when a globalUser correctly guesses the Rule Code.";
    }

    @Override
    public String usage() {
        return "[Role Name/Remove]";
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
}