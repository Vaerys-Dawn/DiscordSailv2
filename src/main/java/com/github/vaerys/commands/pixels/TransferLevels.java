package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.RewardRoleObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class TransferLevels implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.xpGain) {
            return "> Cannot transfer levels, xp gain enabled.";
        }
        if (command.guild.config.getRewardRoles().size() == 0) {
            return "> No rewards available to grant. cannot transfer levels";
        }
        IMessage message = Utility.sendMessage("`Working...`", command.channel.get()).get();

        Utility.sortRewards(command.guild.config.getRewardRoles());
        for (IUser user : command.guild.get().getUsers()) {
            if (!user.isBot()) {
                ProfileObject uObject = command.guild.users.getUserByID(user.getLongID());
                if (uObject == null) {
                    uObject = new ProfileObject(user.getLongID());
                }
                uObject.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
//                uObject.setRewardID(-1);
                uObject.setXp(0);
                uObject.setCurrentLevel(-1);

                for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                    for (IRole uRole : user.getRolesForGuild(command.guild.get())) {
                        if (r.getRoleID() == uRole.getLongID()) {
                            uObject.setXp(XpHandler.totalXPForLevel(r.getLevel()));
//                            uObject.setRewardID(r.getRoleID());
                            uObject.setCurrentLevel(r.getLevel());
                        }
                    }
                }
                XpHandler.checkUsersRoles(uObject.getUserID(), command.guild);
            }
        }
        Utility.deleteMessage(message);
        command.guild.config.xpGain = true;
        return "> Transfer Complete.";
    }

    @Override
    public String[] names() {
        return new String[]{"TransferLevels"};
    }

    @Override
    public String description() {
        return "Allows for the transfer of levels.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
