package com.github.vaerys.handlers;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.pogos.GuildConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vaerys.enums.UserSetting.DENY_AUTO_ROLE;

public class GuildHandler {
    final static Logger logger = LoggerFactory.getLogger(GuildHandler.class);

    public static void dailyTask(GuildObject content) {
        boolean doDecay = content.config.xpGain && content.config.modulePixels && content.config.xpDecay;
        for (ProfileObject p : content.users.getProfiles()) {
            if (doDecay) PixelHandler.doDecay(content, p);
            //check globalUser's roles and make sure that they have the right roles.
            checkUsersRoles(p.getUserID(), content, true);
        }
        checkTopTen(content);
    }

    public static void checkTopTen(GuildObject content) {
        if (!content.config.modulePixels || !content.config.xpGain) return;
        Role topTenRole = content.get().getRoleById(content.config.topTenRoleID);
        if (topTenRole == null) return;
        List<ProfileObject> profiles = new ArrayList<>(content.users.profiles);
        Utility.sortUserObjects(profiles, false);
        int counter = 0;
        //empty non top ten users
        List<UserObject> topTen = content.get().getMembersWithRoles(topTenRole).stream().map(u -> new UserObject(u, content)).collect(Collectors.toList());
        for (UserObject u : topTen) {
            long rank = PixelHandler.rank(content, u);
            if (rank > 10 || rank < 0) {
                content.get().removeRoleFromMember(u.getMember(), topTenRole).queue();
            }
        }
        //check for new top ten users
        List<ProfileObject> newTopTen = new LinkedList<>();
        for (ProfileObject p : profiles) {
            if (counter >= 10) break;
            if (!p.showRank(content)) continue;
            UserObject user = p.getUser(content);
            if (user.get() == null) continue;
            newTopTen.add(p);
            content.get().addRoleToMember(user.getMember(), topTenRole).queue();
            counter++;
        }
    }

    public static void checkUsersRoles(long id, GuildObject content) {
        checkUsersRoles(id, content, false);
    }

    public static void checkUsersRoles(long id, GuildObject content, boolean bulkCheck) {

        //don't try to edit your own roles ya butt.
        if (id == Client.getClientObject().bot.longID) return;

        //do code.
        ProfileObject profile = content.users.getUserByID(id);
        if (profile == null) return;
        if (profile.getSettings().contains(DENY_AUTO_ROLE)) return;

        // skip muted users
        if (content.config.muteRemovesRoles &&
                content.users.getMutedUser(profile.getUserID()) != null) return;

        Member user = content.getUserByID(profile.getUserID());
        if (user == null) return;

        List<Role> userRoles = user.getRoles();
        if (userRoles.contains(content.getMutedRole())) return;

        if (content.config.readRuleReward) {
            Role ruleReward = content.getRoleById(content.config.ruleCodeRewardID);
            if (ruleReward != null) {
                if (profile.getSettings().contains(UserSetting.READ_RULES)) {
                    userRoles.add(ruleReward);
                } else {
                    userRoles.remove(ruleReward);
                }
            }
        }

        if (content.config.modulePixels && content.config.xpGain) {
            //remove all rewardRoles to prep for checking.
            ListIterator iterator = userRoles.listIterator();
            while (iterator.hasNext()) {
                Role role = (Role) iterator.next();
                if (content.config.isRoleReward(role.getIdLong())) {
                    iterator.remove();
                }
            }
            //add all roles that the globalUser should have.
            ArrayList<RewardRoleObject> allRewards = content.config.getAllRewards(profile.getCurrentLevel());
            for (RewardRoleObject r : allRewards) {
                userRoles.add(content.get().getRoleById(r.getRoleID()));
            }
            if (!bulkCheck) {
                //add the top ten role if they should have it.
                Role topTenRole = content.get().getRoleById(content.config.topTenRoleID);
                if (topTenRole != null) {
                    long rank = PixelHandler.rank(content.users, content.get(), user.getIdLong());
                    if (rank <= 10 && rank > 0) {
                        userRoles.add(topTenRole);
                    } else {
                        userRoles.remove(topTenRole);
                    }
                }
            }
        }

        //only do a role update if the role count changes
        List<Role> currentRoles = user.getRoles();
        if (!currentRoles.containsAll(userRoles) || currentRoles.size() != userRoles.size()) {
            content.get().modifyMemberRoles(user, userRoles).queue();
        }
    }

    //Discord Utils


    /**
     * Searches for a role within the guild
     *
     * @param roleName - the full or partial name of the role
     * @param guild    - the guild to search within
     * @return The closest matching role
     */
    public static Role getRoleFromName(String roleName, Guild guild) {
        return getRoleFromName(roleName, guild, false);
    }

    /**
     * Searches for a role within the guild
     *
     * @param roleName   - the full or partial name of the role
     * @param guild      - the guild to search within
     * @param startsWith - Whether the search should look for
     * @return The closest matching role
     */
    public static Role getRoleFromName(String roleName, Guild guild, boolean startsWith) {
        return getRoleFromName(roleName, guild.getRoles(), false);
    }

    /**
     * Searches for a role within the guild
     *
     * @param roleName  - the full or partial name of the role
     * @param guild     - the guild to search within
     * @param filterIds - a list of role ids to filter in
     * @return The closest matching role
     */
    public static Role getRoleFromName(String roleName, Guild guild, List<Long> filterIds) {
        return getRoleFromName(roleName, guild, filterIds, false);
    }

    /**
     * Searches for a role within the guild
     *
     * @param roleName   - the full or partial name of the role
     * @param guild      - the guild to search within
     * @param filterIds  - a list of role ids to filter in
     * @param startsWith - Whether the search should look for
     * @return The closest matching role
     */
    public static Role getRoleFromName(String roleName, Guild guild, List<Long> filterIds, boolean startsWith) {
        List<Role> filtered =
                guild
                        .getRoles()
                        .stream()
                        .filter((Role r) -> {
                            return filterIds.contains(r.getIdLong());
                        })
                        .collect(Collectors.toList());
        return getRoleFromName(roleName, filtered, startsWith);
    }

    /**
     * Searches for a role within the guild
     *
     * @param roleName      - The full or partial name of the role
     * @param rolesToSearch - The list of roles to search through
     * @param startsWith    - Whether the search should look for
     * @return The closest matching role
     */
    public static Role getRoleFromName(String roleName, List<Role> rolesToSearch, boolean startsWith) {
        Role role = null;
        for (Role r : rolesToSearch) {
            if (startsWith) {
                if (r.getName().toLowerCase().startsWith(roleName.toLowerCase())) {
                    role = r;
                    break;
                }
            } else {
                if (r.getName().equalsIgnoreCase(roleName)) {
                    role = r;
                    break;
                }
            }
        }
        return role;
    }

    public static List<Role> getRolesByName(Guild guild, String name) {
        List<Role> roles = guild.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return roles;
    }

    public static Color getUsersColour(Member user, Guild guild) {
        //before
        if (user == null) return Constants.DEFAULT_COLOUR;
        List<Role> userRoles = user.getRoles();
        Role topColour = null;
        String defaultColour = "0,0,0";
        for (Role role : userRoles) {
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
        } else return Constants.DEFAULT_COLOUR;
    }

    public static Color getUsersColour(List<Role> userRoles) {
        Role topColour = null;
        String defaultColour = "0,0,0";
        for (Role role : userRoles) {
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
        } else return Constants.DEFAULT_COLOUR;
    }

    public static boolean canBypass(Member author, Guild guild, boolean logging) {
        if (author == null) return false;
        if (guild == null) return false;
        GuildObject object = Globals.getGuildContent(guild.getIdLong());
        if (object == null) return false;
        GuildConfig config = object.config;
        if (author.getIdLong() == Globals.creatorID && config.debugMode) {
            if (logging) {
                logger.trace("User is Creator, BYPASSING.");
            }
            return true;
        }

        if (author.getIdLong() == guild.getOwnerIdLong()) {
            if (logging) {
                logger.trace("User is Guild Owner, GUILD : \"" + guild.getIdLong() + "\", BYPASSING.");
            }
            return true;
        }
        if (author.getPermissions().contains(Permission.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }

    public static boolean canBypass(Member author, Guild guild) {
        return canBypass(author, guild, true);
    }

    public static boolean canBypass(UserObject user, GuildObject guild) {
        return canBypass(user.getMember(), guild.get());
    }

    public static boolean canBypass(CommandObject command) {
        return canBypass(command.user.getMember(), command.guild.get());
    }

    public static boolean testForPerms(Member user, Guild guild, Permission... perms) {
        if (perms.length == 0) return true;
        if (user == null) return false;
        if (guild == null) return true;
        if (canBypass(user, guild)) return true;
        EnumSet<Permission> toMatch = EnumSet.noneOf(Permission.class);
        toMatch.addAll(Arrays.asList(perms));
//        Debug code.
        List<String> toMatchList = new ArrayList<String>() {{
            addAll(toMatch.stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        List<String> userList = new ArrayList<String>() {{
            addAll(user.getPermissions().stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        if (true) {
            logger.trace("To Match : " + Utility.listFormatter(toMatchList, true));
            logger.trace("User Perms : " + Utility.listFormatter(userList, true));
            logger.trace("Result : " + user.getPermissions().containsAll(toMatch));
        }
//        end Debug
        return user.getPermissions().containsAll(toMatch);
    }

    public static boolean testForPerms(CommandObject object, Permission... perms) {
        return testForPerms(object.user.getMember(), object.guild.get(), perms);
    }

    public static boolean testForPerms(CommandObject object, List<Permission> perms) {
        return testForPerms(object.user.getMember(), object.guild.get(), perms);
    }

    public static boolean testForPerms(UserObject user, GuildObject guild, Permission... perms) {
        return testForPerms(user.getMember(), guild.get(), perms);
    }

    public static boolean testForPerms(UserObject user, GuildObject guild, List<Permission> perms) {
        return testForPerms(user.getMember(), guild.get(), perms);
    }

    public static boolean testForPerms(Member user, Guild guild, List<Permission> perms) {
        return testForPerms(user, guild, perms.toArray(new Permission[perms.size()]));
    }

    public static boolean testForPerms(CommandObject command, MessageChannel channel, Permission... perms) {
        boolean hasPerms = true;
        if (canBypass(command.user.getMember(), command.guild.get())) {
            return true;
        }
        for (Permission p : perms) {
            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                PermissionOverride perm = textChannel.getPermissionOverride(command.user.getMember());
                if (perm == null || !perm.getAllowed().contains(p)) {
                    hasPerms = false;
                }
            }
        }
        return hasPerms;
    }

    public static boolean testForPerms(CommandObject command, ChannelObject channel, Permission... perms) {
        return testForPerms(command, channel.getMessageChannel(), perms);
    }


    public static void toggleRoles(CommandObject command, Role... roles) {
        toggleRoles(command.user.getMember(), command.guild.get(), roles);
    }

    public static void toggleRoles(UserObject user, GuildObject guild, Role... roles) {
        if (roles.length == 0) return;
        if (user == null) return;
        if (guild == null) return;
        List<Role> temp = new ArrayList<>(Arrays.asList(roles));
        ListIterator<Role> iterator = temp.listIterator();
        while (iterator.hasNext()) {
            Role next = iterator.next();
            if (next == null) continue;
            if (user.roles.contains(next)) {
                user.roles.remove(next);
                iterator.remove();
            }
        }
        user.roles.addAll(temp);
        guild.get().modifyMemberRoles(user.getMember(), user.roles).queue();
    }

    public static void toggleRoles(Member user, Guild guild, Role... roles) {
        GuildObject guildObject = Globals.getGuildContent(guild.getIdLong());
        UserObject userObject = new UserObject(user, guildObject);
        toggleRoles(userObject, guildObject, roles);
    }
}

