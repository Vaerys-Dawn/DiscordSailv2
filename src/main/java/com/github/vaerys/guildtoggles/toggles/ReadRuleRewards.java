package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetRuleCode;
import com.github.vaerys.commands.general.RulesCode;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

public class ReadRuleRewards extends GuildSetting {

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
        return "Enables the Rule Code commands which serve the purpose of giving admins a way to see if a user has read the rules, If pixel gain is on, users will also receive a "
                + (long) (200 * command.guild.config.xpModifier) + " pixel boost.";
    }

    @Override
    public void setup() {
        commands.add(new RulesCode());
        commands.add(new SetRuleCode());
    }
}
