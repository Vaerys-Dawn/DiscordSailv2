package com.github.vaerys.commands.roleSelect;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class CosmeticRoles extends Command {

    private static final SubCommandObject EDIT_ROLES = new SubCommandObject(
            new String[]{"Role"},
            "[Role Name]",
            "Used to manage the selectable cosmetic roles.",
            SAILType.ADMIN,
            Permissions.MANAGE_ROLES
    );

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) {
            return get(ListRoles.class).execute(args, command);
        }

        //test to see if the first word is a modifier

        if (EDIT_ROLES.isSubCommand(command)) {
            boolean isAdding = args.split(" ")[0].equals("+");
            //test the permissions of the user to make sure they can modify the role list.
            IRole role = null;

            String subArgs = EDIT_ROLES.getArgs(command);
            try {
                role = command.guild.getRoleByID(Utility.stringLong(subArgs));
            } catch (NumberFormatException e) {
                // move on.
            }
            if (role == null) role = GuildHandler.getRoleFromName(subArgs, command.guild.get());
            if (role == null) return "> **" + subArgs + "** is not a valid Role Name.";

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
            //do user role modification
        } else {
            //check to make sure that the user isn't including the args brackets or the /remove at the end;
            if (command.guild.config.getCosmeticRoleIDs().size() == 0)
                return "> No Cosmetic roles are set up right now. Come back later.";
            if (args.matches("[(|\\[].*[)|\\]]")) {
                return Constants.ERROR_BRACKETS + "\n" + Utility.getCommandInfo(this, command);
            }
            if (args.matches(".*/remove")) {
                return "> Did you mean `" + command.guild.config.getPrefixCommand() + names()[0] + " " + args.replaceAll("(?i)/remove", "") + "`?";
            }
            List<IRole> userRoles = command.user.roles;
            String response;
            //check if role is valid
            IRole role;
            role = GuildHandler.getRoleFromName(args, command.guild.get());
            if (role == null && args.length() > 3) {
                role = GuildHandler.getRoleFromName(args, command.guild.get(), true);
            }
            if (role == null && !args.equalsIgnoreCase("remove")) {
                RequestHandler.sendEmbedMessage("> **" + args + "** is not a valid Role Name.", ListRoles.getList(command), command.channel.get());
                return null;
                //if args = remove. remove the user's cosmetic role
            } else if (args.equalsIgnoreCase("remove")) {
                userRoles = userRoles.stream().filter(r -> !command.guild.config.isRoleCosmetic(r.getLongID())).collect(Collectors.toList());
                if (command.user.getCosmeticRoles(command).size() == 0) return "> You don't have a role to remove...";
                else response = "> You have had your cosmetic role removed.";
            } else {
                //check if role is cosmetic
                if (command.guild.config.isRoleCosmetic(role.getLongID())) {
                    //check to see if roles are toggles
                    if (command.guild.config.roleIsToggle) {
                        //if user has role, remove it.
                        if (userRoles.contains(role)) {
                            userRoles.remove(role);
                            response = "> You have had the **" + role.getName() + "** role removed.";
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
                            userRoles = userRoles.stream().filter(r -> !command.guild.config.isRoleCosmetic(r.getLongID())).collect(Collectors.toList());
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

    @Override
    protected String[] names() {
        return new String[]{"Role"};
    }

    @Override
    public String description(CommandObject command) {
        if (!command.guild.config.roleIsToggle) {
            return "Allows you to choose a role from a list of cosmetic roles.\n" +
                    "You can only have one cosmetic role at a time, when choosing a new role it will remove your old one.\n\n" +
                    "**TIP** You only need the first 4 letters of a role to be able to select it.";
        } else {
            return "Allows you to toggle a cosmetic role. You can have as many cosmetic roles as you like.";
        }

    }

    @Override
    protected String usage() {
        return "(Role Name)/Remove";
    }

    @Override
    protected SAILType type() {
        return SAILType.ROLE_SELECT;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {
        subCommands.add(EDIT_ROLES.appendRegex(" (\\+|-)"));
    }
}
