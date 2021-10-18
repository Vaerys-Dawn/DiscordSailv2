package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.characters.EditDungeonChar;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class DungeonChars extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.DUNGEON_CHARS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dungeonChars = !config.dungeonChars;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.dungeonChars;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dungeonChars;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability to set and see Dungeon Stats.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "enables Dungeon Characters";
    }

    @Override
    public void setup() {
        commands.add(EditDungeonChar.class);
    }
}