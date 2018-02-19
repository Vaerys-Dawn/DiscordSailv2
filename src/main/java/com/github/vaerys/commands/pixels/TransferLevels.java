package com.github.vaerys.commands.pixels;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class TransferLevels extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.xpGain) {
            return "> Cannot transfer levels, xp gain enabled.";
        }
        if (command.guild.config.getRewardRoles().size() == 0) {
            return "> No rewards available to grant. cannot transfer levels";
        }
        IMessage message = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();

        Utility.sortRewards(command.guild.config.getRewardRoles());

        for (IUser user : command.guild.getUsers()) {
            if (!user.isBot()) {
                ProfileObject uObject = command.guild.users.getUserByID(user.getLongID());
                if (uObject == null) {
                    uObject = command.guild.users.addUser(user.getLongID());
                }
                uObject.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
                uObject.setXp(0);
                uObject.setCurrentLevel(-1);
                for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                    if (user.getRolesForGuild(command.guild.get()).contains(r.get(command.guild))) {
                        uObject.setXp(r.getXp());
                        uObject.setCurrentLevel(r.getLevel());
                    }
                }
                XpHandler.checkUsersRoles(uObject.getUserID(), command.guild);
            }
        }
        RequestHandler.deleteMessage(message);
        command.guild.config.xpGain = true;
        return "> Transfer Complete.";
    }

    protected static final String[] NAMES = new String[]{"TransferLevels"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the transfer of levels.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
