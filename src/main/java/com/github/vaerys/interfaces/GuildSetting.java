package com.github.vaerys.interfaces;

import com.github.vaerys.commands.CommandObject;

public interface GuildSetting extends GuildToggle{

    @Override
    default boolean isModule(){
        return false;
    }

    @Override
    default String stats(CommandObject object){
        return null;
    }
}
