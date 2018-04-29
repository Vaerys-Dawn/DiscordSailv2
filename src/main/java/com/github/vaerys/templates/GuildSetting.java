package com.github.vaerys.templates;

import com.github.vaerys.masterobjects.CommandObject;

public abstract class GuildSetting extends GuildToggle {

    @Override
    public boolean isModule() {
        return false;
    }

    @Override
    public String stats(CommandObject command) {
        return null;
    }

    @Override
    public boolean statsOnInfo() {
        return false;
    }
}
