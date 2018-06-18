package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.help.HelpTags;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleAdminCC extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.ADMIN_CC;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleAdminCC = !config.moduleAdminCC;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleAdminCC;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleAdminCC;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows admins to create admin custom commands.\n" +
                "> Admin Custom commands use the **" + command.guild.config.getPrefixAdminCC() + "** command prefix.\n" +
                "> Admin Custom commands are completely user created and they can use the tag system to change how they work.\n" +
                "> For information about the tag system you can run the **" + Command.get(HelpTags.class).getCommand(command) + "** command.";
    }

    @Override
    public void setup() {
    }


    @Override
    public String stats(CommandObject command) {
        return "**Total Admin Custom Commands:** " + command.guild.adminCCs.getCommands().size();

    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows admins to create admin custom commands.";
    }
}
