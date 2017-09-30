package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.EditLinks;
import com.github.vaerys.commands.general.SetGender;
import com.github.vaerys.commands.general.SetQuote;
import com.github.vaerys.commands.general.UserInfo;
import com.github.vaerys.guildtoggles.toggles.UserInfoShowsDate;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleMe implements GuildModule {

    @Override
    public String name() {
        return "Profiles";
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
    public void execute(GuildObject guild) {
        guild.removeCommand(new UserInfo().names());
        guild.removeCommand(new SetGender().names());
        guild.removeCommand(new SetQuote().names());
        guild.removeCommand(new EditLinks().names());
        guild.removeToggle(new UserInfoShowsDate().name());
    }

    @Override
    public String stats(CommandObject object) {
        return "**Total Profiles:** " + object.guild.users.getProfiles().size();
    }
}
