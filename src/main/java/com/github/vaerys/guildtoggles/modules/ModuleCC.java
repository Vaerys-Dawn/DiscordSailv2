package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.channelsettings.settings.Shitpost;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.HelpTags;
import com.github.vaerys.guildtoggles.toggles.ShitpostFiltering;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleCC extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_CC;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleCC = !config.moduleCC;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleCC;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleCC;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows users to create custom commands.\n" +
                "> Custom commands use the **" + command.guild.config.getPrefixCC() + "** command prefix.\n" +
                "> Custom commands are completely user created and they can use the tag system to change how they work.\n" +
                "> For information about the tag system you can run the **" + new HelpTags().getCommand(command) + "** command.";
    }

    @Override
    public void setup() {
        channels.add(new Shitpost());
        settings.add(new ShitpostFiltering());
    }


    @Override
    public String stats(CommandObject object) {
        return "**Total Custom Commands:** " + object.guild.customCommands.getCommandList().size();

    }
}
