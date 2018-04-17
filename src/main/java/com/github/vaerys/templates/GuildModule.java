package com.github.vaerys.templates;

import com.github.vaerys.masterobjects.CommandObject;

public abstract class GuildModule extends GuildToggle {

    public GuildModule() {
        affectsType = name();
    }

    @Override
    public boolean isModule() {
        return true;
    }

    @Override
    public boolean statsOnInfo() {
        return true;
    }

    @Override
    public abstract String shortDesc(CommandObject command);
}
