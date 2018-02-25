package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class ModuleSlash extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.SLASH;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.slashCommands = !config.slashCommands;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.slashCommands;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().slashCommands;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables additional text-based emoji slash commands.";
    }

    @Override
    public void setup() {
    }

    @Override
    public String stats(CommandObject command) {
        return desc(command);
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }
}
