package Commands.General;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetAvatar implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        List<IUser> mentions = command.message.getMentions();
        StringBuilder builder = new StringBuilder();
        for (IUser u : mentions) {
            builder.append(u.getDisplayName(command.guild) + ": " + u.getAvatarURL() + "\n");
        }
        return builder.toString();
    }

    @Override
    public String[] names() {
        return new String[]{"GetAvatar"};
    }

    @Override
    public String description() {
        return "Gets the Mentionee's Profile Image.";
    }

    @Override
    public String usage() {
        return "[@Users...]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
