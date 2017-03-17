package Commands.General;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class SetGender implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IUser user = command.author;
        SplitFirstObject userID = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (Utility.testForPerms(dualPerms(), command.author, command.guild) || Utility.canBypass(command.author, command.guild)) {
            user = command.client.getUserByID(userID.getFirstWord());
            if (user != null) {
                adminEdit = true;
            }
        }

        for (UserTypeObject u : command.guildUsers.getUsers()) {

            if (adminEdit) {
                if (userID.getRest().length() > 20){
                    return "> User Gender's Length is too long...\n(Must be under 20 chars)";
                }
                if (u.getID().equals(user.getID())) {
                    u.setGender(userID.getRest());
                    return "> User's Gender Edited";
                }
            } else {
                if (args.length() > 20) {
                    return "> Your Gender's Length is too long...\n(Must be under 20 chars)";
                }
                if (u.getID().equals(command.authorID)) {
                    u.setGender(args);
                    return "> Gender Edited";

                }
            }
        }
        return "> User Has not Spoken yet thus they have nothing to edit.";
    }

    @Override
    public String[] names() {
        return new String[]{"SetGender"};
    }

    @Override
    public String description() {
        return "Allows you to set your Gender on your User Card.";
    }

    @Override
    public String usage() {
        return "[Gender]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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
        return "Allows you to set the Gender of a user.";
    }

    @Override
    public String dualUsage() {
        return "[UserID] [User Gender]";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }
}
