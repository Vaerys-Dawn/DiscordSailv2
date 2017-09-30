package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleChars implements GuildModule {

    @Override
    public String name() {
        return "Chars";
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
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_CHARACTER);
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
