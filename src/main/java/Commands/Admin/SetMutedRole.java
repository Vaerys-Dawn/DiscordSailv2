package Commands.Admin;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.RoleTypeObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetMutedRole implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("Remove")){
            command.guildConfig.setMutedRole(new RoleTypeObject("NoRoleSetUp","null"));
            return "> Muted Role Removed.";
        }
        String roleID = Utility.getRoleIDFromName(args, command.guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            command.guildConfig.setMutedRole(new RoleTypeObject(command.guild.getRoleByID(roleID).getName(), roleID));
            return "> Role: " + command.guild.getRoleByID(roleID).getName() + " Has been set as the Muted Role.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetMutedRole","MutedRole"};
    }

    @Override
    public String description() {
        return "Sets the Muted role.";
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
