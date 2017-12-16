package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.commands.CommandObject;
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
public class CosmeticRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) {
            return new ListRoles().execute(args, command);
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
                if (!Utility.testUserHierarchy(command.client.bot, role, command.guild.get())) {
                    return "> I do not have permission to modify the **" + role.getName() + "** role.";
                }
                //test the user's hierarchy to make sure that the are allowed to mess with that role.
                if (Utility.testUserHierarchy(command.user.get(), role, command.guild.get())) {
                    // do if modifier is true
                    if (isAdding) {
                        //check for the role and add if its not a cosmetic role.
                        if (command.guild.config.isRoleCosmetic(role.getLongID())) {
                            return "> The **" + role.getName() + "** role is already listed as a cosmetic role.";
                        } else {
                            command.guild.config.getCosmeticRoleIDs().add(role.getLongID());
                            return "> The **" + role.getName() + "** role was added to the cosmetic role list.";
                        }
                        //do if modifier is false
                    } else {
                        //check for the role and remove if it is a cosmetic role.
                        if (command.guild.config.isRoleCosmetic(role.getLongID())) {
                            Iterator iterator = command.guild.config.getCosmeticRoleIDs().listIterator();
                            while (iterator.hasNext()) {
                                long id = (long) iterator.next();
                                if (role.getLongID() == id) {
                                    iterator.remove();
                                }
                            }
                            return "> The **" + role.getName() + "** role was removed from the cosmetic role list.";
                        } else {
                            return "> The **" + role.getName() + "** role is not listed as a cosmetic role.";
                        }
                    }
                } else {
                    return "> You do not have permission to modify the **" + role.getName() + "** role.";
                }
            } else {
                return command.user.notAllowed;
            }
            //do user role modification
        } else {
            //check to make sure that the user isn't including the args brackets or the /remove at the end;
            if (args.startsWith("[") && args.endsWith("]")) {
                return Constants.ERROR_BRACKETS + "\n" + Utility.getCommandInfo(this, command);
            }
            if (args.toLowerCase().endsWith("/remove")) {
                return "> Did you mean `" + command.guild.config.getPrefixCommand() + names()[0] + " " + args.replaceAll("(?i)/remove", "") + "`?";
            }
            List<IRole> userRoles = command.user.roles;
            String response = Constants.ERROR_UPDATING_ROLE;
            //check if role is valid
            IRole role;
            role = Utility.getRoleFromName(args, command.guild.get());
            if (role == null && args.length() > 3) {
                role = Utility.getRoleFromName(args, command.guild.get(), true);
            }
            if (role == null && !args.equalsIgnoreCase("remove")) {
                Utility.sendEmbedMessage("> **" + args + "** is not a valid Role Name.", ListRoles.getList(command), command.channel.get());
                return null;
                //if args = remove. remove the user's cosmetic role
            } else if (args.equalsIgnoreCase("remove")) {
                ListIterator iterator = userRoles.listIterator();
                boolean removedSomething = false;
                while (iterator.hasNext()) {
                    IRole userRole = (IRole) iterator.next();
                    if (command.guild.config.isRoleCosmetic(userRole.getLongID())) {
                        iterator.remove();
                        removedSomething = true;
                    }

                }
                if (removedSomething) {
                    response = "> You have had your cosmetic role removed.";
                } else {
                    response = "> You don't have a role to remove...";
                }
            } else {
                //check if role is cosmetic
                if (command.guild.config.isRoleCosmetic(role.getLongID())) {
                    //check to see if roles are toggles
                    if (command.guild.config.roleIsToggle) {
                        //if user has role, remove it.
                        if (userRoles.contains(role)) {
                            ListIterator iterator = userRoles.listIterator();
                            while (iterator.hasNext()) {
                                IRole userRole = (IRole) iterator.next();
                                if (role.getLongID() == userRole.getLongID()) {
                                    iterator.remove();
                                    response = "> You have been granted the **" + role.getName() + "** role.";
                                }
                            }
                            //else add that role.
                        } else {
                            userRoles.add(role);
                            response = "> You have had the **" + role.getName() + "** role removed.";
                        }
                        //if roles arent toggles run this.
                    } else {
                        //if they already have that role
                        if (userRoles.contains(role)) {
                            return "> You already have the **" + role.getName() + "** role.";
                        } else {
                            //remove all cosmetic role and add the new one
                            ListIterator iterator = userRoles.listIterator();
                            while (iterator.hasNext()) {
                                IRole userRole = (IRole) iterator.next();
                                if (command.guild.config.isRoleCosmetic(userRole.getLongID())) {
                                    iterator.remove();
                                }
                            }
                            userRoles.add(role);
                            response = "> You have selected the cosmetic role: **" + role.getName() + "**.";
                        }
                    }
                } else {
                    Utility.sendEmbedMessage("> **" + args + "** is not a valid cosmetic role.", ListRoles.getList(command), command.channel.get());
                    return null;
                }
            }
            // push the changes to the user's roles.
            if (Utility.roleManagement(command.user.get(), command.guild.get(), userRoles).get()) {
                return response;
            } else {
                return Constants.ERROR_UPDATING_ROLE;
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Role"};
    }

    @Override
    public String description(CommandObject command) {
        return "Modifies your cosmetic role from the list of cosmetic roles.";
    }

    @Override
    public String usage() {
        return "(Role Name)/Remove";
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
    public String dualDescription() {
        return "Used to manage the selectable cosmetic roles.";
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
