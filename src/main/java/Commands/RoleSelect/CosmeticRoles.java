package Commands.RoleSelect;

import Commands.Command;
import Commands.CommandObject;
import GuildToggles.GuildToggle;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.RoleTypeObject;
import Objects.SplitFirstObject;
import POGOs.GuildConfig;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class CosmeticRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        IUser author = command.author;
        IGuild guild = command.guild;
        List<IRole> oldRoles = new ArrayList<>(command.author.getRolesForGuild(guild));
        GuildConfig guildConfig = command.guildConfig;
        String response;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        if (splitFirst.getFirstWord() != null) {
            if (Utility.testModifier(splitFirst.getFirstWord()) != null && Utility.testForPerms(dualPerms(), author, guild)) {
                if (Utility.testModifier(splitFirst.getFirstWord())) {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), true);
                    }
                } else {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), true);
                    }
                }
            } else {
                ArrayList<RoleTypeObject> roles = guildConfig.getCosmeticRoles();
                String newRoleId = null;
                List<IRole> userRoles = guild.getRolesForUser(author);
                for (RoleTypeObject role : roles) {
                    for (int i = 0; userRoles.size() > i; i++) {
                        if (role.getRoleID().equals(userRoles.get(i).getID())) {
                            userRoles.remove(i);
                        }
                        if (args.equalsIgnoreCase(guild.getRoleByID(role.getRoleID()).getName())) {
                            newRoleId = role.getRoleID();
                        }
                    }
                }
                if (splitFirst.getFirstWord().equalsIgnoreCase("remove")) {
                    response = "> Your Cosmetic role was removed.";
                } else {
                    if (newRoleId == null) {
                        if (Utility.testModifier(splitFirst.getFirstWord()) != null) {
                            return command.notAllowed;
                        }
                        return "> Role with name: **" + args + "** not found in **Cosmetic Role** list.";
                    } else {
                        userRoles.add(guild.getRoleByID(newRoleId));
                        response = "> You have selected the cosmetic role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
                    }
                }
                command.client.getDispatcher().dispatch(new UserRoleUpdateEvent(guild,author,oldRoles,userRoles));
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
        return new String[]{"Role"};
    }

    @Override
    public String description() {
        return "Sets your cosmetic role from the list of cosmetic roles.";
    }

    @Override
    public String usage() {
        return "[Role Name]/Remove";
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
