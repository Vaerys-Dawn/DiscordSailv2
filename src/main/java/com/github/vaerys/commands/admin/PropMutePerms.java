package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.cache.LongMap;

import java.util.EnumSet;
import java.util.List;

public class PropMutePerms extends Command {
    private final static Logger logger = LoggerFactory.getLogger(PropMutePerms.class);

    @Override
    public String execute(String args, CommandObject command) {
        // check if bot has perms
        EnumSet<Permissions> botPerms = command.client.bot.getPermissions(command.guild);
        if (!botPerms.contains(Permissions.MANAGE_CHANNELS)) {
            return "> I do not have permission to run this command. I need to have Manage Channels.";
        }

        // get current channel's "Muted" role perms
        IRole mutedRole = command.guild.getMutedRole();
        if (mutedRole == null) return "> No muted role set.";
        LongMap<PermissionOverride> roleOverrides = command.channel.get().getRoleOverrides();
        if (!roleOverrides.containsKey(mutedRole.getLongID())) return "> No modified permissions for " + mutedRole.getName() + " in this channel.";

        IMessage workingMsg = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();

        PermissionOverride mutedPermissions = roleOverrides.get(mutedRole.getLongID());
        List<IChannel> guildChannels = command.guild.get().getChannels();
        int counter = 0;
        for (IChannel channel : guildChannels) {
            // remove old permissions, then add our stored set.
            channel.removePermissionsOverride(mutedRole);
            channel.overrideRolePermissions(mutedRole, mutedPermissions.allow(), mutedPermissions.deny());
            counter++;
        }

        RequestHandler.deleteMessage(workingMsg);
        return "> Set permissions for " + counter + " channels to:\n**Allow**:" + mutedPermissions.allow().toString() +
                "\n**Deny**:" + mutedPermissions.deny().toString();
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateMutePerms", "SyncMutePerms"};
    }

    @Override
    public String description(CommandObject command) {
        return "Syncs the current channel's \"muted\" role permissions to all of the other channels on this server.\n" +
                "You will need to set the muted role on your server for this command to work.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS, Permissions.MANAGE_ROLES};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
