package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.commands.admin.SetWelcomeMessage;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class WelcomeMessages extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.JOIN_SERVER_MESSAGES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.welcomeMessages = !config.welcomeMessages;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.welcomeMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().welcomeMessages;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the sending of a welcome message to globalUser's dms when they join the server.";
    }

    @Override
    public void setup() {
        commands.add(SetWelcomeMessage.class);
    }
}
