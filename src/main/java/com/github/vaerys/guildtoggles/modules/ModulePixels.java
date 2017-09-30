package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.DenyXpPrefix;
import com.github.vaerys.guildtoggles.toggles.ReactToLevelUp;
import com.github.vaerys.guildtoggles.toggles.SelfDestructLevelUps;
import com.github.vaerys.guildtoggles.toggles.XpDecay;
import com.github.vaerys.guildtoggles.toggles.XpGain;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ModulePixels implements GuildModule {
    @Override
    public String name() {
        return "Pixels";
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
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_PIXEL);
        guild.removeCommand(new DenyXpPrefix().names());
        guild.removeToggle(new XpDecay().name());
        guild.removeToggle(new XpGain().name());
        guild.removeToggle(new SelfDestructLevelUps().name());
        guild.removeToggle(new ReactToLevelUp().name());
    }

    @Override
    public String stats(CommandObject object) {
        boolean hasManageServer = Utility.testForPerms(object, Permissions.MANAGE_SERVER);
        StringBuilder builder = new StringBuilder();
        builder.append("**Pixels Per Message: ** " + object.guild.config.xpRate);
        builder.append("\n**Pixel Modifier:** " + object.guild.config.xpModifier);
        if (hasManageServer) {
            IRole topTen = object.guild.get().getRoleByID(object.guild.config.topTenRoleID);
            IRole xpDenied = object.guild.get().getRoleByID(object.guild.config.xpDeniedRoleID);
            if (topTen != null)
                builder.append("\n**Top Ten Role:** " + topTen.getName());
            if (xpDenied != null)
                builder.append("\n**Xp Denied Role:** " + xpDenied.getName());
        }
        if (object.guild.config.getRewardRoles().size() != 0) {
            builder.append("\n\n**[REWARD ROLES]**");
            for (RewardRoleObject r : object.guild.config.getRewardRoles()) {
                IRole role = object.guild.get().getRoleByID(r.getRoleID());
                if (role != null) {
                    builder.append("\n**" + role.getName() + "** - Lvl " + r.getLevel());
                }
            }
        }
        return builder.toString();
    }
}
