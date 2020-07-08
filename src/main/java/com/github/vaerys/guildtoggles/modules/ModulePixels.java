package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.commands.pixels.DenyXpPrefix;
import com.github.vaerys.commands.pixels.PixelHelp;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.Role;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ModulePixels extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.PIXEL;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.modulePixels = !config.modulePixels;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.modulePixels;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().modulePixels;
    }

    @Override
    public String desc(CommandObject command) {
        if (command.guild.get() == null) {
            return "Error, you should not get this message. if you do please report this to the bot developer.";
        }
        return "This module enables **" + command.client.bot.displayName + "'s** XP system known as pixels.\n" +
                "\\> Pixels are a xp system that allows the **granting of roles** at certain levels.\n" +
                "\\> When a user levels up a **level up message** will be sent to them based on specific settings.\n" +
                "\\> Level up messages can be **customised completely**, from where they are sent by default to the text that they send.\n" +
                "\\> Pixels are able to be granted **once per minute** to users when they send messages.\n" +
                "\\> The amount of pixels that are given per chunk can be customised via a **settable multiplier**.\n" +
                "\\> An optional **decay system** also exists to remove pixels from users who become inactive.\n\n" +
                "**Stats:**\n" +
                "To see stats of this module you will need to run either the **" + new GetGuildInfo().getCommand(command) +
                "** or **" + new PixelHelp().getCommand(command) + "** commands.\nThe **" + new PixelHelp().getCommand(command) + "** command will also give you a lot more information about this module.";
    }

    @Override
    public void setup() {
        commands.add(DenyXpPrefix.class);

        channels.add(ChannelSetting.LEVEL_UP);
        channels.add(ChannelSetting.PIXELS);
        channels.add(ChannelSetting.LEVEL_UP_DENIED);
        channels.add(ChannelSetting.XP_DENIED);

        settings.add(SAILType.XP_DECAY);
        settings.add(SAILType.XP_GAIN);
        settings.add(SAILType.SELF_DESTRUCT_LEVEL_UPS);
        settings.add(SAILType.REACT_TO_LEVEL_UP);
        settings.add(SAILType.LIKE_ART);
    }

    @Override
    public String stats(CommandObject command) {
        boolean hasManageServer = GuildHandler.testForPerms(command, Permissions.MANAGE_SERVER);
        StringBuilder builder = new StringBuilder();
        builder.append("**Pixels Per Message: ** " + command.guild.config.xpRate);
        builder.append("\n**Pixel Modifier:** " + command.guild.config.xpModifier);
        if (hasManageServer) {
            Role topTen = command.guild.getRoleById(command.guild.config.topTenRoleID);
            Role xpDenied = command.guild.getRoleById(command.guild.config.xpDeniedRoleID);
            if (topTen != null)
                builder.append("\n**Top Ten Role:** " + topTen.getName());
            if (xpDenied != null)
                builder.append("\n**Xp Denied Role:** " + xpDenied.getName());
        }
        if (command.guild.config.getRewardRoles().size() != 0) {
            builder.append("\n\n**[REWARD ROLES]**");
            for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                Role role = command.guild.getRoleById(r.getRoleID());
                if (role != null) {
                    builder.append("\n**" + role.getName() + "** - Lvl " + r.getLevel());
                }
            }
        }
        return builder.toString();
    }

    @Override
    public boolean statsOnInfo() {
        return false;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to get EXP, as \"pixels\" for server activity.";
    }
}
