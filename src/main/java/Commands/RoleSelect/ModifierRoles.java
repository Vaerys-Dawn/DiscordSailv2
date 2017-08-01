package Commands.RoleSelect;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ModifierRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject modif = new SplitFirstObject(args);

        //test to see if the first word is a modifier
        Boolean isAdding = Utility.testModifier(modif.getFirstWord());
        if (isAdding != null) {
            //test the permissions of the user to make sure they can modify the role list.
            if (Utility.testForPerms(dualPerms(), command.author, command.guild)) {
                IRole role = Utility.getRoleFromName(modif.getRest(), command.guild);
                if (role == null) {
                    return "> **" + args + "** is not a valid Role Name.";
                } else {
                    //tests to see if the bot is allowed to mess with a role.
                    if (!Utility.testUserHierarchy(command.botUser,role,command.guild)){
                        return "> I do not have permission to modify the **" + role.getName() + "** role.";
                    }
                    //test the user's hierarchy to make sure that the are allowed to mess with that role.
                    if (Utility.testUserHierarchy(command.author, role, command.guild)) {
                        // do if modifier is true
                        if (isAdding) {
                            //check for the role and add if its not a Modifier role.
                            if (command.guildConfig.isRoleModifier(role.getLongID())) {
                                return "> The **" + role.getName() + "** role is already listed as a modifier role.";
                            } else {
                                command.guildConfig.getModifierRoleIDs().add(role.getLongID());
                                return "> The **" + role.getName() + "** role was added to the modifier role list.";
                            }
                            //do if modifier is false
                        } else {
                            //check for the role and remove if it is a Modifier role.
                            if (command.guildConfig.isRoleModifier(role.getLongID())) {
                                Iterator iterator = command.guildConfig.getModifierRoleIDs().listIterator();
                                while (iterator.hasNext()) {
                                    long id = (long) iterator.next();
                                    if (role.getLongID() == id) {
                                        iterator.remove();
                                    }
                                }
                                return "> The **" + role.getName() + "** role was removed from the modifier role list.";
                            } else {
                                return "> The **" + role.getName() + "** role is not listed as a modifier role.";
                            }
                        }
                    } else {
                        return "> You do not have permission to modify the **" + role.getName() + "**role.";
                    }
                }
            } else {
                return command.notAllowed;
            }
        } else {
            IRole role = Utility.getRoleFromName(args, command.guild);
            List<IRole> userRoles = command.authorRoles;
            String response = Constants.ERROR_UPDATING_ROLE;
            if (role == null) {
                return "> **" + args + "** is not a valid Role Name.";
            } else {
                if (command.guildConfig.isRoleModifier(role.getLongID())) {
                    //if user has role remove it
                    if (userRoles.contains(role)) {
                        ListIterator iterator = userRoles.listIterator();
                        while (iterator.hasNext()) {
                            IRole userRole = (IRole) iterator.next();
                            if (userRole.getLongID() == role.getLongID()) {
                                iterator.remove();
                                response = "> You have had the **" + role.getName() + "** role removed.";
                            }
                        }
                        //else add it
                    } else {
                        userRoles.add(role);
                        response = "> You have been granted the **" + role.getName() + "** role.";
                    }
                    //push changes
                    if (Utility.roleManagement(command.author, command.guild, userRoles).get()) {
                        return response;
                    } else {
                        return Constants.ERROR_UPDATING_ROLE;
                    }
                } else {
                    return "> The **" + role.getName() + "** role is not listed as a modifier role.";
                }
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Modifier", "Modif"};
    }

    @Override
    public String description() {
        return "Allows you to toggle a modifier role.";
    }

    @Override
    public String usage() {
        return "[Role Name]";
    }

    @Override
    public String type() {
        return TYPE_ROLE_SELECT;
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
        return "Used to manage the selectable modifier roles.";
    }

    @Override
    public String dualUsage() {
        return "+/-/add/del [Role Name]";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_ROLES};
    }
}
