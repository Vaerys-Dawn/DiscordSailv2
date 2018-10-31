package com.github.vaerys.objects;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.IIDLinkedObject;
import sx.blah.discord.handle.obj.IRole;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 17/11/2016.
 */


/* Notes:
 * Character names will now have to be unique and cannot contain spaces.
 * The base commands that are with v1 are the first ones being created
 * the rest will come later. (not top priority sadly :c)
 */

public class CharacterObject {
    String name; //Character's Name
    long userID; //author's authorSID
    String nickname; //character's name;
    ArrayList<Long> roleIDs = new ArrayList<>(); // these are the cosmetic and modifier roleIDs the author has;
    String gender = "N/a"; //limit = 20 chars.
    String age = "Unknown"; // limit = 20 chars.
    String shortBio = ""; //limit to 140 chars.
    String avatarURL = "";
    String longBioURL = ""; //URL link linking to Character Bios
    private String weight = null;
    private String height = null;

    public CharacterObject(String name, long userID, String nickname, List<Long> roleIDs) {
        this.name = name;
        this.userID = userID;
        this.nickname = nickname;
        this.roleIDs = (ArrayList<Long>) roleIDs;
    }

    public String getLongBioURL() {
        return longBioURL;
    }

    public void setLongBioURL(String longBioURL) {
        this.longBioURL = longBioURL;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public long getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public List<Long> getRoleIDs() {
        return roleIDs;
    }

    public void setRoleIDs(ArrayList<Long> roleIDs) {
        this.roleIDs = roleIDs;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public void update(String name, List<IRole> roles) {
        this.nickname = name;
        this.roleIDs = new ArrayList<>(roles.stream().map(IIDLinkedObject::getLongID).collect(Collectors.toList()));
    }

    public Color getColor(GuildObject guild) {
        return GuildHandler.getUsersColour(roleIDs.stream().map(aLong -> guild.getRoleByID(aLong)).filter(iRole -> iRole != null).collect(Collectors.toList()));
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }
}
