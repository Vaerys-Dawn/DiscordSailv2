package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Objects.RewardRoleObject;
import Objects.SplitFirstObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class SetLevel implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject xpArgs = new SplitFirstObject(args);
        try {
            long level = Long.parseLong(xpArgs.getFirstWord());
            long xp = XpHandler.totalXPForLevel(level);
            if (command.message.getMentions().size() == 0) {
                return "> no user found";
            } else {
                IUser user = command.message.getMentions().get(0);
                UserTypeObject userObject = command.guildUsers.getUserByID(user.getStringID());
                if (userObject != null) {
                    userObject.setXp(xp);
                    RewardRoleObject reward = XpHandler.rewardForLevel(userObject.getCurrentLevel(),command);
                    if (reward != null){
                        userObject.setRewardID(reward.getRoleID());
                        XpHandler.checkUsersRoles(user.getStringID(),command.guildContent);
                    }
                    return "> User's Level is now set to: **" + xpArgs.getFirstWord() + "**";
                } else {
                    return "> an error occurred.";
                }
            }
        }catch(NumberFormatException e){
            return "> Invalid number";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetLevel"};
    }

    @Override
    public String description() {
        return "Allows you to set the level of a user.";
    }

    @Override
    public String usage() {
        return "[Level] [@User]";
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
        return true;
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
