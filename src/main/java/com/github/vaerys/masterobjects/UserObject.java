package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wrapper for the User API object. Contains All bot information linked to that globalUser for the Guild assigned to it.
 */

public class UserObject extends GlobalUserObject {

    public String displayName;
    protected Member member;
    public List<Role> roles;
    protected GuildObject guild;

    public UserObject(Member object, GuildObject guild) {
        super(object.getUser());
        member = object;
        initMember(member, guild);
    }

    public UserObject(User object, GuildObject guild) {
        super(object);
        member = guild.get().retrieveMember(object).complete();
        initMember(member, guild);
    }

    public UserObject(GlobalUserObject globalUser, GuildObject guild) {
        super(globalUser.object);
        member = guild.get().retrieveMember(object).complete();
        initMember(member, guild);
    }

    public UserObject(long userID, GuildObject guild) {
        super(userID);
        member = guild.get().retrieveMemberById(longID).complete();
        initMember(member, guild);
    }

    protected void initMember(Member member, GuildObject guild) {
        this.guild = guild;
        displayName = member.getEffectiveName();
        roles = member.getRoles();
        escapeMentions();
        notAllowed = "\\> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
    }

    protected void escapeMentions() {
        Pattern pattern = Pattern.compile("(?i)(@everyone|@here|<@[!|&]?[0-9]*>)");
        Matcher nameMatcher = pattern.matcher(name);
        Matcher displayMatcher = pattern.matcher(displayName);

        if (nameMatcher.find() || displayMatcher.find()) {
            name = name.replace("@", "@" + Command.SPACER);
            displayName = displayName.replace("@", "@" + Command.SPACER);
            username = username.replace("@", "@" + Command.SPACER);
        }
    }

    public List<TextChannel> getVisibleChannels(List<TextChannel> channels) {
        return channels.stream().filter(c -> c.canTalk(member)).collect(Collectors.toList());
    }

    public ProfileObject getProfile() {
        ProfileObject profile = guild.users.getUserByID(longID);
        if (profile == null && (member != null && !isBot)) {
            profile = guild.users.addUser(longID);
        }
        if (profile != null && profile.getSettings() != null && !profile.getSettings().isEmpty()) {
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
