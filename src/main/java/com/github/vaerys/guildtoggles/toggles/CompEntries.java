package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CompEntries extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.COMP_ENTRIES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.compEntries = !config.compEntries;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.compEntries;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().compEntries;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability to allow users to submit entries to the currently active competition.";
    }

    @Override
    public void setup() {

    }
}
