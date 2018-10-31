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

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ModifierRoles extends Command {

    protected static final SubCommandObject EDIT_ROLES = new SubCommandObject(
            new String[]{"Modifier", "Modif"},
            "+/-/add/del [Role Name]",
            "Used to manage the selectable modifier roles.",
            SAILType.ADMIN,
            Permissions.MANAGE_ROLES
    );

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) {
            return get(ListModifs.class).execute(args, command);
        }
//        SplitFirstObject modif = new SplitFirstObject(args);

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
                return "> You do not have permission to modify the **" + role.getName() + "** role.";
            }
            //do user role modification
        } else {
            if (command.guild.config.getModifierRoleIDs().size() == 0)
                return "> No Modifier roles are set up right now. Come back later.";
            IRole role;
            role = GuildHandler.getRoleFromName(args, command.guild.get());
            if (role == null && args.length() > 3) {
                role = GuildHandler.getRoleFromName(args, command.guild.get(), true);
            }
            List<IRole> userRoles = command.user.roles;
            String response;
            if (role == null) {
                RequestHandler.sendEmbedMessage("> **" + args + "** is not a valid Role Name.", ListModifs.getList(command), command.channel.get());
                return null;
            } else {
                if (command.guild.config.isRoleModifier(role.getLongID())) {
                    //if user has role remove it
                    if (userRoles.contains(role)) {
                        userRoles.remove(role);
                        response = "> You have had the **" + role.getName() + "** role removed.";
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
    protected String[] names() {
        return new String[]{"Modifier", "Modif"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to toggle a modifier role. You can have as many modifier roles as you like.\n\n" +
                "**TIP** You only need the first 4 letters of a role to be able to select it.";
    }

    @Override
    protected String usage() {
        return "(Role Name)";
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
