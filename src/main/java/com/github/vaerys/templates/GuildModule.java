package com.github.vaerys.templates;

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



}
