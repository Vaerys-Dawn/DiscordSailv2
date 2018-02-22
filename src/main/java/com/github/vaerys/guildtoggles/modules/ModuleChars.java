package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.toggles.RoleIsToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleChars extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.CHARACTER;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleChars = !config.moduleChars;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleChars;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleChars;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows users to create and use character profiles.\n" +
                "> This module is primarily role play based.\n" +
                "> Character profiles store the role and nickname when created or updated.\n" +
                "> Users can also assign extra information to each character.";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.CHARACTER);
        settings.add(new RoleIsToggle());
    }

    @Override
    public String stats(CommandObject command) {
        StringBuilder builder = new StringBuilder();
        builder.append("**Total Characters:** " + command.guild.characters.getCharacters(command.guild.get()).size());
        if (Utility.testForPerms(command, Permissions.MANAGE_SERVER)) {
            builder.append("\n**Character Roles Prefix:** " + command.guild.characters.getRolePrefix());
        }
        return builder.toString();
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allow users to create character profiles for role-playing.";
    }
}
