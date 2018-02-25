package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.pogos.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.vaerys.enums.UserSetting.DENY_AUTO_ROLE;

public class GuildHandler {

    final static Logger logger = LoggerFactory.getLogger(GuildHandler.class);

    public static void dailyTask(GuildObject content) {
        for (ProfileObject p : content.users.getProfiles()) {

            if (content.config.xpGain && content.config.modulePixels && content.config.xpDecay) {
                XpHandler.doDecay(content, p);
            }
            //check user's roles and make sure that they have the right roles.
            checkUsersRoles(p.getUserID(), content);
        }
    }

    public static void checkUsersRoles(long id, GuildObject content) {

        //do code.
        ProfileObject profile = content.users.getUserByID(id);
        if (profile == null) {
            return;
        }
        if (profile.getSettings().contains(DENY_AUTO_ROLE)) return;

        IUser user = Globals.getClient().getUserByID(profile.getUserID());
        if (user == null) {
            return;
        }
        List<IRole> userRoles = user.getRolesForGuild(content.get());

        if (content.config.readRuleReward) {
            IRole ruleReward = content.getRoleByID(content.config.ruleCodeRewardID);
            if (ruleReward != null) userRoles.add(ruleReward);
        }

        if (content.config.modulePixels && content.config.xpGain) {
            //remove all rewardRoles to prep for checking.
            ListIterator iterator = userRoles.listIterator();
            while (iterator.hasNext()) {
                IRole role = (IRole) iterator.next();
                if (content.config.isRoleReward(role.getLongID())) {
                    iterator.remove();
                }
            }
            //add all roles that the user should have.
            ArrayList<RewardRoleObject> allRewards = content.config.getAllRewards(profile.getCurrentLevel());
            for (RewardRoleObject r : allRewards) {
                userRoles.add(Globals.getClient().getRoleByID(r.getRoleID()));
            }
            //add the top ten role if they should have it.
            IRole topTenRole = content.get().getRoleByID(content.config.topTenRoleID);
            if (topTenRole != null) {
                long rank = XpHandler.rank(content.users, content.get(), user.getLongID());
                if (rank <= 10 && rank > 0) {
                    userRoles.add(topTenRole);
                }
            }
        }

        //only do a role update if the role count changes
        List<IRole> currentRoles = user.getRolesForGuild(content.get());
        if (!currentRoles.containsAll(userRoles) || currentRoles.size() != userRoles.size()) {
            RequestHandler.roleManagement(user, content.get(), userRoles);
        }
    }

    //Discord Utils
    public static IRole getRoleFromName(String roleName, IGuild guild, boolean startsWith) {
        IRole role = null;
        for (IRole r : guild.getRoles()) {
            if (startsWith) {
                if (r.getName().toLowerCase().startsWith(roleName.toLowerCase())) {
                    role = r;
                }
            } else {
                if (r.getName().equalsIgnoreCase(roleName)) {
                    role = r;
                }
            }
        }
        return role;
    }

    public static List<IRole> getRolesByName(IGuild guild, String name) {
        List<IRole> roles = guild.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return roles;
    }

    public static IRole getRoleFromName(String roleName, IGuild guild) {
        return getRoleFromName(roleName, guild, false);
    }

    public static boolean testForPerms(CommandObject command, IChannel channel, Permissions... perms) {
        boolean hasPerms = true;
        if (canBypass(command.user.get(), command.guild.get())) {
            return true;
        }
        for (Permissions p : perms) {
            if (!channel.getModifiedPermissions(command.user.get()).contains(p)) {
                hasPerms = false;
            }
        }
        return hasPerms;
    }

    public static Color getUsersColour(IUser user, IGuild guild) {
        //before
        List<IRole> userRoles = guild.getRolesForUser(user);
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return null;
    }

    public static Color getUsersColour(List<IRole> userRoles) {
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return Color.black;
    }

    public static boolean canBypass(IUser author, IGuild guild, boolean logging) {
        GuildConfig config = Globals.getGuildContent(guild.getLongID()).config;
        if (author.getLongID() == Globals.creatorID && config.debugMode) {
            if (logging) {
                logger.trace("User is Creator, BYPASSING.");
            }
            return true;
        }
        if (guild == null) {
            return false;
        }
        if (author.getLongID() == guild.getOwnerLongID()) {
            if (logging) {
                logger.trace("User is Guild Owner, GUILD : \"" + guild.getLongID() + "\", BYPASSING.");
            }
            return true;
        }
        if (author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }

    public static boolean canBypass(IUser author, IGuild guild) {
        return canBypass(author, guild, true);
    }

    public static boolean canBypass(UserObject user, GuildObject guild) {
        return canBypass(user.get(), guild.get());
    }

    public static boolean testForPerms(IUser user, IGuild guild, Permissions... perms) {
        if (perms.length == 0) return true;
        if (guild == null) return true;
        if (canBypass(user, guild)) return true;
        EnumSet<Permissions> toMatch = EnumSet.noneOf(Permissions.class);
        toMatch.addAll(Arrays.asList(perms));
//        Debug code.
        List<String> toMatchList = new ArrayList<String>() {{
            addAll(toMatch.stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        List<String> userList = new ArrayList<String>() {{
            addAll(user.getPermissionsForGuild(guild).stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        if (true) {
            logger.trace("To Match : " + Utility.listFormatter(toMatchList, true));
            logger.trace("User Perms : " + Utility.listFormatter(userList, true));
            logger.trace("Result : " + user.getPermissionsForGuild(guild).containsAll(toMatch));
        }
//        end Debug
        return user.getPermissionsForGuild(guild).containsAll(toMatch);
    }

    public static boolean testForPerms(CommandObject object, Permissions... perms) {
        return testForPerms(object.user.get(), object.guild.get(), perms);
    }

    public static boolean testForPerms(UserObject user, GuildObject guild, Permissions... perms) {
        return testForPerms(user.get(), guild.get(), perms);
    }
}
