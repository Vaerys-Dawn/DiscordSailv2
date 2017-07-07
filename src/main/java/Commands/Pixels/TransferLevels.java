package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Utility;
import Objects.RewardRoleObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class TransferLevels implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guildConfig.xpGain) {
            return "> Cannot transfer levels, xp gain enabled.";
        }
        if (command.guildConfig.getRewardRoles().size() == 0) {
            return "> No rewards available to grant. cannot transfer levels";
        }
        IMessage message = Utility.sendMessage("`Working...`", command.channel).get();

        Utility.sortRewards(command.guildConfig.getRewardRoles());
        for (IUser user : command.guild.getUsers()) {
            if (!user.isBot()) {
                UserTypeObject uObject = command.guildUsers.getUserByID(user.getStringID());
                if (uObject == null) {
                    uObject = new UserTypeObject(user.getStringID());
                }
                uObject.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
                uObject.setRewardID(-1);
                uObject.setXp(0);
                uObject.setCurrentLevel(-1);

                for (RewardRoleObject r : command.guildConfig.getRewardRoles()) {
                    for (IRole uRole : user.getRolesForGuild(command.guild)) {
                        if (r.getRoleID() == uRole.getLongID()) {
                            uObject.setXp(XpHandler.totalXPForLevel(r.getLevel()));
                            uObject.setRewardID(r.getRoleID());
                            uObject.setCurrentLevel(r.getLevel());
                        }
                    }
                }
            }
        }
        Utility.deleteMessage(message);
        command.guildConfig.xpGain = true;
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
