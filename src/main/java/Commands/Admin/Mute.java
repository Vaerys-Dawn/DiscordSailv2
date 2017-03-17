package Commands.Admin;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class Mute implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject userCall = new SplitFirstObject(args);
        IRole mutedRole = command.client.getRoleByID(command.guildConfig.getMutedRole().getRoleID());
        if (mutedRole == null) {
            return "> Cannot Mute/UnMute user. No mute role exists.";
        }
        if (userCall.getRest() != null) {
            SplitFirstObject modifier = new SplitFirstObject(userCall.getRest());
            IUser muted;
            if (command.message.getMentions().size() > 0) {
                muted = command.message.getMentions().get(0);
            } else {
                muted = command.client.getUserByID(userCall.getFirstWord());
                if (muted == null) {
                    return "> Cannot Mute/UnMute user. User ID invalid.";
                }
            }
            if (muted.getID().equals(command.authorID)){
                return "> Don't try to mute yourself you numpty.";
            }
            if (Utility.testModifier(modifier.getFirstWord()) != null) {
                if (Utility.testUserHierarchy(command.author, muted, command.guild)) {
                    if (Utility.testModifier(modifier.getFirstWord())) {
                        long timeSecs = -1;
                        if (modifier.getRest() != null) {
                            SplitFirstObject time = new SplitFirstObject(modifier.getRest());
                            timeSecs = Utility.textToSeconds(time.getFirstWord());
                        }
                        command.guildUsers.muteUser(muted.getID(), timeSecs, command.guildID);
                        if (timeSecs == -1) {
                            return "> User Was Muted.";
                        } else {
                            return "> User Was Muted for " + timeSecs + " Seconds.";
                        }
                    }else {
                        command.guildUsers.unMuteUser(muted.getID(),command.guildID);
                        return "> User UnMuted.";
                    }
                } else {
                    return "> Cannot Mute/UnMute user, User hierarchy higher than yours.";
                }
            } else {
                return "> Cannot Mute/UnMute user. Modifier Invalid. Must be either +/-/add/del";
            }
        } else {
            return "> Cannot Mute/UnMute user. No modifier specified.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Mute"};
    }

    @Override
    public String description() {
        return "Allows for Users with ManageMessages to mute a user.";
    }

    @Override
    public String usage() {
        return "[@user/UserID] [+/-/add/del] (Time) (Reason)";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
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
