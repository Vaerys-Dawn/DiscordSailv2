package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.general.EditLinks;
import com.github.vaerys.commands.general.SetGender;
import com.github.vaerys.commands.general.SetQuote;
import com.github.vaerys.commands.general.UserInfo;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.guildtoggles.toggles.UserInfoShowsDate;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;

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
    public boolean enabled(GuildConfig config) {
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
        commands.add(Command.get(UserInfo.class));
        commands.add(Command.get(SetGender.class));
        commands.add(Command.get(SetQuote.class));
        commands.add(Command.get(EditLinks.class));
        settings.add(ToggleList.getSetting(SAILType.USER_INFO_SHOWS_DATE));
    }

    @Override
    public String stats(CommandObject command) {
        return "**Total Profiles:** " + command.guild.users.getProfiles().size();
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Generates personal profiles that users can edit.";
    }
}
