package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.SetJoinMessage;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class JoinServerMessages extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.JOIN_SERVER_MESSAGES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinsServerMessages = !config.joinsServerMessages;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.joinsServerMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinsServerMessages;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the sending of a welcome message to user's dms when they join the server.";
    }

    @Override
    public void setup() {
        commands.add(new SetJoinMessage());
    }
}
