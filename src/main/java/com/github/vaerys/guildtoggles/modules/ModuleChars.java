package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleChars extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_CHARACTER;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleChars = !config.moduleChars;
    }

    @Override
    public boolean get(GuildConfig config) {
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
    }

    @Override
    public String stats(CommandObject object) {
        StringBuilder builder = new StringBuilder();
        builder.append("**Total Characters:** " + object.guild.characters.getCharacters(object.guild.get()).size());
        if (Utility.testForPerms(object, Permissions.MANAGE_SERVER)) {
            builder.append("\n**Character Roles Prefix:** " + object.guild.characters.getRolePrefix());
        }
        return builder.toString();
    }
}
