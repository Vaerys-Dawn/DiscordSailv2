package com.github.vaerys.objects;

import java.util.ArrayList;

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
    String userID; //author's authorSID
    String nickname; //character's name;
    ArrayList<Long> roleIDs = new ArrayList<>(); // these are the cosmetic and modifier roleIDs the author has;
    String gender = "N/a"; //limit = 20 chars.
    String age = "Unknown"; // limit = 20 chars.
    String shortBio = ""; //limit to 140 chars.
    String avatarURL = "";
    String longBioURL = ""; //URL link linking to Character Bios

    public CharacterObject(String name, String userID, String nickname, ArrayList<Long> roleIDs) {
        this.name = name;
        this.userID = userID;
        this.nickname = nickname;
        this.roleIDs = roleIDs;
    }

    public void setLongBioURL(String longBioURL) {
        this.longBioURL = longBioURL;
    }

    public String getLongBioURL() {
        return longBioURL;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public void setUsername(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRoleIDs(ArrayList<Long> roleIDs) {
        this.roleIDs = roleIDs;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Long> getRoleIDs() {
        return roleIDs;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void update(CharacterObject newCharacter) {
        this.nickname = newCharacter.getNickname();
        this.roleIDs = newCharacter.getRoleIDs();
    }
}
