package Commands.RoleSelect;

import Interfaces.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Utility;
import Objects.RoleTypeObject;
import Objects.SplitFirstObject;
import POGOs.GuildConfig;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ModifierRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IGuild guild = command.guild;
        IUser author = command.author;
        List<IRole> oldRoles = new ArrayList<>(command.author.getRolesForGuild(guild));
        GuildConfig guildConfig = command.guildConfig;
        String response;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        boolean rolefound = false;
        if (splitFirst.getFirstWord() != null) {
            if (Utility.testModifier(splitFirst.getFirstWord()) != null && Utility.testForPerms(dualPerms(), author, guild)) {
                if (Utility.testModifier(splitFirst.getFirstWord())) {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), false);
                    }
                } else {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), false);
                    }
                }
            } else {
                ArrayList<RoleTypeObject> roles = guildConfig.getModifierRoles();
                String newRoleId = null;
                List<IRole> userRoles = guild.getRolesForUser(author);
                ArrayList<String> roleNames1 = new ArrayList<>();
                for (IRole r : userRoles) {
                    roleNames1.add(r.getName());
                }
                int userRoleCount = userRoles.size();
                for (RoleTypeObject role : roles) {
                    if (args.equalsIgnoreCase(guild.getRoleByID(role.getRoleID()).getName())) {
                        newRoleId = role.getRoleID();
                    }
                }
                for (int i = 0; userRoles.size() > i; i++) {
                    if (userRoles.get(i).getID().equals(newRoleId)) {
                        userRoles.remove(i);
                        rolefound = true;
                    }
                }
                if (newRoleId == null) {
                    if (Utility.testModifier(splitFirst.getFirstWord()) != null) {
                        return command.notAllowed;
                    }
                    return "> Role with name: **" + args + "** not found in **Modifier Role** list.";
                } else {
                    //adding
                    if (!rolefound) {
                        userRoles.add(guild.getRoleByID(newRoleId));
                        if (userRoleCount >= userRoles.size()) {
                            ArrayList<String> roleNames2 = new ArrayList<>();
                            for (IRole r : userRoles) {
                                roleNames2.add(r.getName());
                            }
                            return "> An Error Occurred while attempting to update your roles.\n" +
                                    "please send this to the Bot Developer.\n" +
                                    "```Old Roles: " + Utility.listFormatter(roleNames1, true).replace("@everyone","#everyone") + "\n" +
                                    "New Roles: " + Utility.listFormatter(roleNames2, true).replace("@everyone","#everyone") + "```";
                        }
                    } else {
                        //removing
                        if (userRoleCount - 2 >= userRoles.size()) {
                            ArrayList<String> roleNames2 = new ArrayList<>();
                            for (IRole r : userRoles) {
                                roleNames2.add(r.getName());
                            }
                            return "> An Error Occurred while attempting to update your roles.\n" +
                                    "please send this to the Bot Developer.\n" +
                                    "```Old Roles." + Utility.listFormatter(roleNames1, true) + "\n" +
                                    "New Roles." + Utility.listFormatter(roleNames2, true) + "```";
                        }
                    }command.client.getDispatcher().dispatch(new UserRoleUpdateEvent(guild,author,oldRoles,userRoles));
                    response = "> You have toggled the Modifier role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
                }
                if (Utility.roleManagement(author, guild, userRoles).get()) {
                    return Constants.ERROR_UPDATING_ROLE;
                } else {
                    return response;
                }
            }
        }
        return Constants.ERROR;
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
