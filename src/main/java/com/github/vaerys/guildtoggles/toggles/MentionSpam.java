package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MentionSpam extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.MENTION_SPAM;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.maxMentions = !config.maxMentions;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.maxMentions;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().maxMentions;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the mention spam prevention feature, any message with 8 or more mentions will be automatically deleted.";
    }

    @Override
    public void setup() {

    }
}
