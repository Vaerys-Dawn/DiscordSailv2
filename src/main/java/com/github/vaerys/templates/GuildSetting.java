package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;

public abstract class GuildSetting extends GuildToggle{

    @Override
    public boolean isModule(){
        return false;
    }

    @Override
    public String stats(CommandObject object){
        return null;
    }

    @Override
    public boolean statsOnInfo() {
        return false;
    }
}
