package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class PropMutePerms extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        // check if bot has perms
        EnumSet<Permission> botPerms = command.botUser.getPermissions();
        if (!botPerms.contains(Permission.MANAGE_CHANNEL)) {
            return "\\> I do not have permission to run this command. I need to have **Manage Channels**.\n" +
                    "Feel free to remove the permission after I am done as I will no longer need it.";
        }
        // get current messageChannel's "Muted" role perms
        Role mutedRole = command.guild.getMutedRole();
        if (mutedRole == null) return "\\> No muted role set.";
        List<PermissionOverride> roleOverrides = command.guildChannel.get().getRolePermissionOverrides();
        Optional<PermissionOverride> mutedRoleOverride = roleOverrides.stream().filter(p -> p.getRole().getIdLong() == mutedRole.getIdLong()).findFirst();

        if (!mutedRoleOverride.isPresent()) return "\\> No modified Permission for " + mutedRole.getName() + " in this messageChannel.";

        Message workingMsg = command.guildChannel.sendMessage("`Working...`");

        StringHandler extraComments = new StringHandler();
        List<TextChannel> guildChannels = command.guild.get().getTextChannels();
        int counter = 0;
        for (TextChannel channel : guildChannels) {
            ChannelManager manager = channel.getManager();
            // remove old Permission, then add our stored set.
            if (!manager.getChannel().getPermissionOverride(command.botUser.getMember()).getAllowed().contains(Permission.MANAGE_PERMISSIONS)) {
                if (extraComments.isEmpty()) {
                    extraComments.append("\\> Could not apply the Permission to the following channels:");
                }
                extraComments.append("\n" + channel.getAsMention());
            } else {
                channel.getManager().removePermissionOverride(mutedRole);
                channel.getManager().putPermissionOverride(mutedRole, mutedRoleOverride.get().getAllowed(), mutedRoleOverride.get().getDenied());
                counter++;
            }
        }

        workingMsg.delete().complete();
        return "\\> Set Permission for " + counter + " channels to:\n**Allow**:" + mutedRoleOverride.get().getAllowed().toString() +
                "\n**Deny**:" + mutedRoleOverride.get().getAllowed().toString() +
                (extraComments.isEmpty() ? "" : "\n" + extraComments) +
                "\n\nYou are now free to remove my **Manage Channels** Permission as I no longer need it.";
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateMutePerms", "SyncMutePerms"};
    }

    @Override
    public String description(CommandObject command) {
        return "Syncs the current messageChannel's \"muted\" role Permission to all of the other channels on this server.\n" +
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
    public Permission[] perms() {
        return new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES};
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
