package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class Voting extends GuildSetting {

    @Override
    public String name() {
        return "Voting";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.compVoting = !config.compVoting;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.compVoting;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().compVoting;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability for users to vote in the currently active competition.";
    }

    @Override
    public void setup() {

    }
}
