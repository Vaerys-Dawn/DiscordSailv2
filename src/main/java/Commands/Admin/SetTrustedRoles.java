package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SetTrustedRoles implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);
        Boolean isAdding = Utility.testModifier(split.getFirstWord());
        if (isAdding != null) {
            String roleID = Utility.getRoleIDFromName(split.getRest(), command.guild);
            if (roleID == null) {
                return Constants.ERROR_ROLE_NOT_FOUND;
            }
            if (isAdding) {
                command.guildConfig.addTrusted(roleID);
                return "> Added role to Trusted Roles.";
            } else {
                command.guildConfig.delTrusted(roleID);
                return "> Removed role from Trusted Roles.";
            }
        } else {
            return Utility.getCommandInfo(this,command);
        }
    }

    @Override
    public String[] names() {
        return new String[]{"TrustedRole"};
    }

    @Override
    public String description() {
        return "add or remove a Trusted role.";
    }

    @Override
    public String usage() {
        return "+/-/add/del [Role name]";
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
        return new Permissions[]{Permissions.MANAGE_ROLES};
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
