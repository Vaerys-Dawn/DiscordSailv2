package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class SendJoinMessages extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.SEND_JOIN_MESSAGES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.sendJoinMessages = !config.sendJoinMessages;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.sendJoinMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().sendJoinMessages;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the sending of Custom join messages.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables Custom Join Messages.";
    }

    @Override
    public void setup() {

    }
}