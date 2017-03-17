package Commands.Admin;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetAdminRole implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Remove")){
            command.guildConfig.setRoleToMention("NoRoleSetUp","null");
            return "> Admin Role Removed.";
        }
        String roleID = Utility.getRoleIDFromName(args, command.guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return command.guildConfig.setRoleToMention(command.guild.getRoleByID(roleID).getName(), roleID);
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetAdminRole","SetRoleToMention","AdminRole"};
    }

    @Override
    public String description() {
        return "Sets the admin role that will be mentioned when the tag #admin# is used in the blacklisting process.";
    }

    @Override
    public String usage() {
        return "[Role Name/Remove]";
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
        return new Permissions[]{Permissions.MANAGE_ROLES,Permissions.MANAGE_SERVER};
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
