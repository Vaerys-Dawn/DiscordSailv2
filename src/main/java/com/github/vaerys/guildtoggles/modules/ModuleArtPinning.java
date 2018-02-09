package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.channelsettings.types.Art;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetPinLimit;
import com.github.vaerys.guildtoggles.toggles.AutoArtPinning;
import com.github.vaerys.guildtoggles.toggles.LikeArt;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class ModuleArtPinning extends GuildModule {

    @Override
    public String name() {
        return "ArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.artPinning = !config.artPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.artPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().artPinning;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows users to pin their art to the specified art channel using the \uD83D\uDCCC reaction.\n" +
                "Users can unpin their artworks with a ❌ reaction.";
    }

    @Override
    public void setup() {
        channels.add(new Art());
        commands.add(new SetPinLimit());
        settings.add(new AutoArtPinning());
        settings.add(new LikeArt());
    }

    @Override
    public String stats(CommandObject object) {
        if (!Utility.testForPerms(object, Permissions.MANAGE_SERVER)) return null;
        return "**Total Pins:** " + object.guild.channelData.getPinnedMessages().size() + "/" + object.guild.config.pinLimit;
    }
}
