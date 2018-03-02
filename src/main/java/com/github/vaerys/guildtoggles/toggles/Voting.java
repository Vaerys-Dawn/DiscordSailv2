package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class Voting extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.VOTING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.compVoting = !config.compVoting;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.compVoting;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().compVoting;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability for users to vote in the currently active competition.";
    }

    @Override
    public void setup() {

    }
}
