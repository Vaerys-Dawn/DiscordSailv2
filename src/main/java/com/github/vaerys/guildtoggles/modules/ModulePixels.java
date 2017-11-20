package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.channelsettings.settings.LevelUpDenied;
import com.github.vaerys.channelsettings.settings.Pixels;
import com.github.vaerys.channelsettings.settings.XpDenied;
import com.github.vaerys.channelsettings.types.LevelUp;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.DenyXpPrefix;
import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.commands.pixels.PixelHelp;
import com.github.vaerys.guildtoggles.toggles.ReactToLevelUp;
import com.github.vaerys.guildtoggles.toggles.SelfDestructLevelUps;
import com.github.vaerys.guildtoggles.toggles.XpDecay;
import com.github.vaerys.guildtoggles.toggles.XpGain;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ModulePixels extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_PIXEL;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.modulePixels = !config.modulePixels;
    }

    @Override
    public boolean get(GuildConfig config) {
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
        return "This module enables **" + command.client.bot.getDisplayName(command.guild.get()) + "'s** XP system known as pixels.\n" +
                "> Pixels are a xp system that allows the **granting of roles** at certain levels.\n" +
                "> When a user levels up a **level up message** will be sent to them based on specific settings.\n" +
                "> Level up messages can be **customised completely**, from where they are sent by default to the text that they send.\n" +
                "> Pixels are able to be granted **once per minute** to users when they send messages.\n" +
                "> The amount of pixels that are given per chunk can be customised via a **settable multiplier**.\n" +
                "> An optional **decay system** also exists to remove pixels from users who become inactive.\n\n" +
                "**Stats:**\n" +
                "To see stats of this module you will need to run either the **" + new GetGuildInfo().getCommand(command) +
                "** or **" + new PixelHelp().getCommand(command) + "** commands.\nThe **" + new PixelHelp().getCommand(command) + "** command will also give you a lot more information about this module.";
    }

    @Override
    public void setup() {
        commands.add(new DenyXpPrefix());
        channels.add(new LevelUp());
        channels.add(new Pixels());
        channels.add(new LevelUpDenied());
        channels.add(new XpDenied());
        settings.add(new XpDecay());
        settings.add(new XpGain());
        settings.add(new SelfDestructLevelUps());
        settings.add(new ReactToLevelUp());
    }

    @Override
    public String stats(CommandObject object) {
        boolean hasManageServer = Utility.testForPerms(object, Permissions.MANAGE_SERVER);
        StringBuilder builder = new StringBuilder();
        builder.append("**Pixels Per Message: ** " + object.guild.config.xpRate);
        builder.append("\n**Pixel Modifier:** " + object.guild.config.xpModifier);
        if (hasManageServer) {
            IRole topTen = object.guild.getRoleByID(object.guild.config.topTenRoleID);
            IRole xpDenied = object.guild.getRoleByID(object.guild.config.xpDeniedRoleID);
            if (topTen != null)
                builder.append("\n**Top Ten Role:** " + topTen.getName());
            if (xpDenied != null)
                builder.append("\n**Xp Denied Role:** " + xpDenied.getName());
        }
        if (object.guild.config.getRewardRoles().size() != 0) {
            builder.append("\n\n**[REWARD ROLES]**");
            for (RewardRoleObject r : object.guild.config.getRewardRoles()) {
                IRole role = object.guild.getRoleByID(r.getRoleID());
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
}
