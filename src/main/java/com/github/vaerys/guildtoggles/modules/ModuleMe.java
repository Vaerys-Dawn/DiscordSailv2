package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.EditLinks;
import com.github.vaerys.commands.general.SetGender;
import com.github.vaerys.commands.general.SetQuote;
import com.github.vaerys.commands.general.UserInfo;
import com.github.vaerys.guildtoggles.toggles.UserInfoShowsDate;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleMe extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.PROFILES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleMe = !config.moduleMe;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleMe;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleMe;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module automatically generates a profile for each user that they can then customise.";
    }

    @Override
    public void setup() {
        commands.add(new UserInfo());
        commands.add(new SetGender());
        commands.add(new SetQuote());
        commands.add(new EditLinks());
        settings.add(new UserInfoShowsDate());
    }

    @Override
    public String stats(CommandObject object) {
        return "**Total Profiles:** " + object.guild.users.getProfiles().size();
    }
}
