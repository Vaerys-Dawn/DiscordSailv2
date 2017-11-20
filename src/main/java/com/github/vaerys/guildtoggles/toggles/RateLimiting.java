package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetRateLimit;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class RateLimiting extends GuildSetting {

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
    public String desc(CommandObject command) {
        return "Enables the ability to set a custom rate limit for the server.";
    }

    @Override
    public void setup() {
        commands.add(new SetRateLimit());
    }
}
