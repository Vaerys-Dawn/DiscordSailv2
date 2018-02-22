package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetPinLimit;
import com.github.vaerys.guildtoggles.toggles.AutoArtPinning;
import com.github.vaerys.guildtoggles.toggles.LikeArt;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class ModuleArtPinning extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.ART_PINNING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.artPinning = !config.artPinning;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.artPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().artPinning;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows users to pin their art to the specified art channel using the \uD83D\uDCCC reaction.\n" +
                "Users can unpin their artworks with a â�Œ reaction.";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.ART);
        commands.add(new SetPinLimit());
        settings.add(new AutoArtPinning());
        settings.add(new LikeArt());
    }

    @Override
    public String stats(CommandObject command) {
        if (!Utility.testForPerms(command, Permissions.MANAGE_SERVER)) return null;
        return "**Total Pins:** " + command.guild.channelData.getPinnedMessages().size() + "/" + command.guild.config.pinLimit;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to pin art with a \uD83D\uDCCC reaction.";
    }
}

// "**moduleName** - shortDesc"
