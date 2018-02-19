package com.github.vaerys.commands.roleSelect;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class CosmeticRoles extends Command {
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
            if (Utility.testForPerms(command, SUB_1.getPermissions())) {
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
            if (command.guild.config.getCosmeticRoleIDs().size() == 0)
                return "> No Cosmetic roles are set up right now. Come back later.";
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
                RequestHandler.sendEmbedMessage("> **" + args + "** is not a valid Role Name.", ListRoles.getList(command), command.channel.get());
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
                                    response = "> You have had the **" + role.getName() + "** role removed.";
                                }
                            }
                            //else add that role.
                        } else {
                            userRoles.add(role);
                            response = "> You have been granted the **" + role.getName() + "** role.";
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
                    RequestHandler.sendEmbedMessage("> **" + args + "** is not a valid cosmetic role.", ListRoles.getList(command), command.channel.get());
                    return null;
                }
            }
            // push the changes to the user's roles.
            if (RequestHandler.roleManagement(command.user.get(), command.guild.get(), userRoles).get()) {
                return response;
            } else {
                return Constants.ERROR_UPDATING_ROLE;
            }
        }
    }

    protected static final String[] NAMES = new String[]{"Role"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        if (!command.guild.config.roleIsToggle) {
            return "Allows you to choose a role from a list of cosmetic roles.\n" +
                    "You can only have one cosmetic role at a time, when choosing a new role it will remove your old one.";
        } else {
            return "Allows you to toggle a cosmetic role. You can have as many cosmetic roles as you like.";
        }

    }

    protected static final String USAGE = "(Role Name)/Remove";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ROLE_SELECT;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.BOT_COMMANDS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    protected static final SubCommandObject SUB_1 = new SubCommandObject(
        NAMES,
        "+/-/add/del [Role Name]",
        "Used to manage the selectable cosmetic roles.",
        SAILType.ADMIN,
        new Permissions[]{Permissions.MANAGE_ROLES}
    );
    @Override
    public void init() {
        subCommands.add(SUB_1);
    }
}
