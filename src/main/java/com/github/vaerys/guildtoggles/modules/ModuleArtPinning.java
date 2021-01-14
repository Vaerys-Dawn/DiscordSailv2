package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.admin.SetPinLimit;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import net.dv8tion.jda.api.Permission;

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
        return "This module allows users to pin their art to the specified art messageChannel using the \uD83D\uDCCC reaction.\n" +
                "Users can unpin their artworks with a \u274C reaction.";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.ART);
        commands.add(SetPinLimit.class);
        settings.add(SAILType.AUTO_ART_PINNING);
        settings.add(SAILType.LIKE_ART);
    }

    @Override
    public String stats(CommandObject object) {
        if (!GuildHandler.testForPerms(object, Permission.MANAGE_SERVER)) return null;
        return "**Total Pins:** " + object.guild.channelData.getPinnedMessages().size() + "/" + object.guild.config.pinLimit;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to pin art with a \uD83D\uDCCC reaction.";
    }
}

// "**moduleName** - shortDesc"
