package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wrapper for the User API object. Contains All bot information linked to that user for the Guild assigned to it.
 */

public class UserObject extends GlobalUserObject {

    public String displayName;
    private Member member;
    public List<Role> roles;
    private GuildObject guild;


    public UserObject(Member object, GuildObject guild) {
        super(object.getUser());
        member = object;
        initMember(member, guild);
    }

    public UserObject(User object, GuildObject guild) {
        super(object);
        member = guild.get().getMember(object);
        initMember(member, guild);
    }

    public UserObject(GlobalUserObject globalUser, GuildObject guild) {
        super(globalUser.object);
        member = guild.get().getMember(object);
        initMember(member, guild);
    }

    public UserObject(long userID, GuildObject guild) {
        super(userID);
        member = guild.get().getMemberById(longID);
        initMember(member, guild);
    }

    private void initMember(Member member, GuildObject guild) {
        this.guild = guild;
        displayName = member.getNickname();
        roles = member.getRoles();
        escapeMentions();
        notAllowed = "\\> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
    }

    private void escapeMentions() {
        Pattern pattern = Pattern.compile("(?i)(@everyone|@here|<@[!|&]?[0-9]*>)");
        Matcher nameMatcher = pattern.matcher(name);
        Matcher displayMatcher = pattern.matcher(displayName);

        if (nameMatcher.find() || displayMatcher.find()) {
            name = name.replace("@", "@" + Command.spacer);
            displayName = displayName.replace("@", "@" + Command.spacer);
            username = username.replace("@", "@" + Command.spacer);
        }
    }

    public List<TextChannel> getVisibleChannels(List<TextChannel> channels) {
        List<Permission> neededPerms = Arrays.asList(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
        return channels.stream().filter(c -> c.getPermissionOverride(member).getAllowed().containsAll(neededPerms)).collect(Collectors.toList());
    }

    public ProfileObject getProfile() {
        ProfileObject profile = guild.users.getUserByID(longID);
        if (profile == null && (member != null && !isBot)) {
            profile = guild.users.addUser(longID);
        }
        if (profile != null && profile.getSettings() != null && profile.getSettings().size() != 0) {
            profile.setSettings(new ArrayList<>(profile.getSettings().stream().distinct().collect(Collectors.toList())));
        }
        return profile;
    }

    public boolean isPrivateProfile() {
        ProfileObject profile = getProfile();
        if (profile == null) {
            return false;
        }
        if (profile.getSettings() == null || profile.getSettings().size() == 0) {
            return false;
        } else {
            return getProfile().getSettings().contains(UserSetting.PRIVATE_PROFILE);
        }
    }

    public boolean showRank() {
        return PixelHandler.rank(guild.users, guild.get(), longID) != -1;
    }



    public String mention() {
        return member.getAsMention();
    }

    public boolean isBlockedFromDms() {
        return Globals.getGlobalData().getBlockedFromDMS().contains(longID);
    }

    public boolean isDecaying() {
        return getProfile().daysDecayed(guild) > 7;
    }

    public ProfileObject addProfile() {
        return guild.users.addUser(longID);
    }

    public int getRewardValue() {
        return PixelHandler.getRewardCount(guild, longID);
    }

    public List<Role> getCosmeticRoles() {
        return roles.stream().filter(iRole -> guild.config.isRoleCosmetic(iRole.getIdLong())).collect(Collectors.toList());
    }

    public EnumSet<Permission> getPermissions() {
        return member.getPermissions();
    }

    public Member getMember() {
        return member;
    }
}
