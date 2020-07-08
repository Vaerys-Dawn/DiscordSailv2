package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Message;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class TransferLevels extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.xpGain) {
            return "\\> Cannot transfer levels, xp gain enabled.";
        }
        if (command.guild.config.getRewardRoles().size() == 0) {
            return "\\> No rewards available to grant. cannot transfer levels";
        }
        Message message = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();

        Utility.sortRewards(command.guild.config.getRewardRoles());

        for (IUser user : command.guild.getUsers()) {
            if (!user.isBot()) {
                ProfileObject uObject = command.guild.users.getUserByID(user.getIdLong());
                if (uObject == null) {
                    uObject = command.guild.users.addUser(user.getIdLong());
                }
                uObject.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
                uObject.setXp(0);
                uObject.setCurrentLevel(0);
                for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                    if (user.getRolesForGuild(command.guild.get()).contains(r.get(command.guild))) {
                        uObject.setXp(r.getXp());
                        uObject.setCurrentLevel(r.getLevel());
                    }
                }
                GuildHandler.checkUsersRoles(uObject.getUserID(), command.guild);
            }
        }
        RequestHandler.deleteMessage(message);
        command.guild.config.xpGain = true;
        return "\\> Transfer Complete.";
    }

    @Override
    protected String[] names() {
        return new String[]{"TransferLevels"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the transfer of levels.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
