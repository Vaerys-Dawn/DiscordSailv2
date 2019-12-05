package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;

public class ModuleDefault extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.DEFAULT;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return true;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return true;
    }

    @Override
    public boolean getDefault() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "If you somehow see this, you have found a bug.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "If you somehow see this, you have found a bug.";
    }

    @Override
    public void setup() {
        settings.add(SAILType.DEBUG_MODE);
        settings.add(SAILType.DAILY_MESSAGE);
        settings.add(SAILType.JOIN_SERVER_MESSAGES);
        settings.add(SAILType.DENY_INVITES);
    }

    @Override
    public String stats(CommandObject command) {
        return null;
    }
}
