package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetRuleCode;
import com.github.vaerys.commands.admin.SetRuleCodeReward;
import com.github.vaerys.commands.general.RulesCode;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

public class ModuleRuleRewards extends GuildModule {

    // TODO: 25/01/2018 add role giving for getting teh code right

    @Override
    public SAILType name() {
        return SAILType.READ_RULES_REWARDS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.readRuleReward = !config.readRuleReward;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.readRuleReward;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().readRuleReward;
    }

    @Override
    public String desc(CommandObject command) {
        return "Allows users to guess a special code to receive special rewards.\n" +
                "> A star on their profile.\n" +
                "> " + (long) (200 * command.guild.config.xpModifier) + " Pixels if Pixels are enabled.\n" +
                "> A Special Role if set up with **" + new SetRuleCodeReward().getUsage(command) + "**.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to gain rewards for reading the rules.";
    }

    @Override
    public void setup() {
        commands.add(new RulesCode());
        commands.add(new SetRuleCode());
        commands.add(new SetRuleCodeReward());
    }

    @Override
    public String stats(CommandObject object) {
        if (GuildHandler.testForPerms(object, Permissions.MANAGE_SERVER)) {
            String response = "";
            if (object.guild.config.getRuleCode() != null) {
                response += "**Rule Code:** " + object.guild.config.getRuleCode();
            }
            IRole reward = object.guild.getRoleByID(object.guild.config.ruleCodeRewardID);
            if (reward != null) {
                if (!response.isEmpty()) response += "\n";
                response += "**Reward Role:** " + reward.getName();
            }
            if (response.isEmpty()) return null;
            return response;
        }
        return null;
    }
}
