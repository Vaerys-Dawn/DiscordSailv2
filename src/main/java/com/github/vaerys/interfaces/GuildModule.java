package com.github.vaerys.interfaces;

public interface GuildModule extends GuildToggle {
    @Override
    default boolean isModule() {
        return true;
    }
}
