package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.admin.SetRateLimit;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class RateLimiting implements GuildToggle {

    @Override
    public String name() {
        return "RateLimiting";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.rateLimiting = !config.rateLimiting;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.rateLimiting;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().rateLimiting;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommand(new SetRateLimit().names());
    }

    @Override
    public boolean isModule() {
        return false;
    }
}
