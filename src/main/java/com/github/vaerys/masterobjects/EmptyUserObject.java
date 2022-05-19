package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.userlevel.ProfileObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class EmptyUserObject extends UserObject {
    public EmptyUserObject(GuildObject guild) {
        super(guild.getBot(), guild);
    }

    @Override
    protected void initMember(Member member, GuildObject guild) {
        this.guild = guild;
        displayName = "UNKNOWN USER";
        roles = new ArrayList<>();
        escapeMentions();
        notAllowed = "\\> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
    }

    @Override
    protected void init(User user) {
        object = user;
        longID = user.getIdLong();
        name = "NULL";
        username = "UNKNOWN_USER#0000";
        color = Color.WHITE;
        isPatron = false;
        isBot = false;
        notAllowed = "\\> I'm sorry " + name + ", I'm afraid I can't let you do that.";
        avatarURL = Utility.getDefaultAvatarURL(user.getIdLong());
        creationDate = Instant.EPOCH;
    }

    @Override
    public List<TextChannel> getVisibleChannels(List<TextChannel> channels) {
        return new ArrayList<>();
    }

    @Override
    public ProfileObject getProfile() {
        return new ProfileObject(longID);
    }

    public boolean isPrivateProfile() {
        return false;
    }

    public boolean showRank() {
        return false;
    }


    public String mention() {
        return "@UNKNOWN_USER#0000";
    }

    public boolean isDecaying() {
        return false;
    }

    public ProfileObject addProfile() {
        return new ProfileObject(longID);
    }

    public int getRewardValue() {
        return 0;
    }

    public List<Role> getCosmeticRoles() {
        return new LinkedList<>();
    }

    public EnumSet<Permission> getPermissions() {
        return EnumSet.noneOf(Permission.class);
    }

    public Member getMember() {
        return member;
    }
}
