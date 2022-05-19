package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class ModuleGroups extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.GROUPS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleGroups = !config.moduleGroups;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleGroups;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleGroups;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module is to help user find other people to play together by letting them group up with each other.";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.GROUPS);
    }

    @Override
    public String stats(CommandObject command) {
        return null;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Helps users find other people willing to group up.";
    }
}
