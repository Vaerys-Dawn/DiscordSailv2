package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.HelpTags;
import com.github.vaerys.guildtoggles.toggles.ShitpostFiltering;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleCC extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.CC;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleCC = !config.moduleCC;
    }

    @Override
    public boolean enabled(GuildConfig config) {
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
        channels.add(ChannelSetting.SHITPOST);
        settings.add(new ShitpostFiltering());
        channels.add(ChannelSetting.CC_INFO);
        channels.add(ChannelSetting.CC_DENIED);
        channels.add(ChannelSetting.MANAGE_CC);
    }


    @Override
    public String stats(CommandObject command) {
        return "**Total Custom Commands:** " + command.guild.customCommands.getCommandList().size();

    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to create their own custom commands.";
    }
}
