package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ModifierRoles extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) {
            return new ListModifs().execute(args, command);
        }
        SplitFirstObject modif = new SplitFirstObject(args);

        //test to see if the first word is a modifier
        Boolean isAdding = Utility.testModifier(modif.getFirstWord());
        if (isAdding != null) {
            //test the permissions of the user to make sure they can modify the role list.
            if (Utility.testForPerms(command, dualPerms())) {
                IRole role = null;
                try {
                    role = command.guild.getRoleByID(Utility.stringLong(modif.getRest()));
                } catch (NumberFormatException e) {
                    // move on.
                }
                if (role == null) {
                    role = Utility.getRoleFromName(modif.getRest(), command.guild.get());
                }
                if (role == null) {
                    return "> **" + modif.getRest() + "** is not a valid Role Name.";
                }
                //tests to see if the bot is allowed to mess with a role.
                if (!Utility.testUserHierarchy(command.client.bot.get(), role, command.guild.get())) {
                    return "> I do not have permission to modify the **" + role.getName() + "** role.";
                }
                //test the user's hierarchy to make sure that the are allowed to mess with that role.
                if (Utility.testUserHierarchy(command.user.get(), role, command.guild.get())) {
                    // do if modifier is true
                    if (isAdding) {
                        //check for the role and add if its not a Modifier role.
                        if (command.guild.config.isRoleModifier(role.getLongID())) {
                            return "> The **" + role.getName() + "** role is already listed as a modifier role.";
                        } else {
                            command.guild.config.getModifierRoleIDs().add(role.getLongID());
                            return "> The **" + role.getName() + "** role was added to the modifier role list.";
                        }
                        //do if modifier is false
                    } else {
                        //check for the role and remove if it is a Modifier role.
                        if (command.guild.config.isRoleModifier(role.getLongID())) {
                            Iterator iterator = command.guild.config.getModifierRoleIDs().listIterator();
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
            } else {
                return command.user.notAllowed;
            }
        } else {
            if (command.guild.config.getModifierRoleIDs().size() == 0) return "> No Modifier roles are set up right now. Come back later.";
            IRole role;
            role = Utility.getRoleFromName(args, command.guild.get());
            if (role == null && args.length() > 3) {
                role = Utility.getRoleFromName(args, command.guild.get(), true);
            }
            List<IRole> userRoles = command.user.roles;
            String response = Constants.ERROR_UPDATING_ROLE;
            if (role == null) {
                RequestHandler.sendEmbedMessage("> **" + args + "** is not a valid Role Name.", ListModifs.getList(command), command.channel.get());
                return null;
            } else {
                if (command.guild.config.isRoleModifier(role.getLongID())) {
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
                    if (RequestHandler.roleManagement(command.user.get(), command.guild.get(), userRoles).get()) {
                        return response;
                    } else {
                        return Constants.ERROR_UPDATING_ROLE;
                    }
                } else {
                    RequestHandler.sendEmbedMessage("> The **" + role.getName() + "** role is not listed as a modifier role.", ListModifs.getList(command), command.channel.get());
                    return null;
                }
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Modifier", "Modif"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to toggle a modifier role. You can have as many modifier roles as you like.";
    }

    @Override
    public String usage() {
        return "(Role Name)";
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
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

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
