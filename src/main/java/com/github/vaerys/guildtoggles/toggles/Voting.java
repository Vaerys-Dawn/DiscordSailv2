package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class Voting implements GuildSetting {

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
    public void execute(GuildObject guild) {

    }
}
