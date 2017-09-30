package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class ModuleSlash implements GuildModule {
    @Override
    public String name() {
        return "SlashCommands";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.slashCommands = !config.slashCommands;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.slashCommands;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().slashCommands;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_SLASH);
    }

    @Override
    public String stats(CommandObject object) {
        return null;
    }
}
