package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class LikeArt extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.LIKE_ART;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.likeArt = !config.likeArt;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.likeArt;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().likeArt;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables art 'liking' feature.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the art liking feature, When this feature is enabled if a ❤ " +
                "reaction is added to an image pinned by the art pinning feature will grant a small amount of pixels to the author.";
    }

    @Override
    public void setup() {

    }
}
