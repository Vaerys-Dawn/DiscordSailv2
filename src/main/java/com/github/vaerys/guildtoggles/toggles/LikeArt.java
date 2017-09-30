package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

public class LikeArt implements GuildSetting{
    @Override
    public String name() {
        return "LikeArt";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.likeArt = !config.likeArt;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.likeArt;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().likeArt;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
