package Objects;

import java.util.ArrayList;
import java.util.List;

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
    String userID; //author's userID
    String nickname; //character's name;
    ArrayList<RoleTypeObject> roles = new ArrayList<>(); // these are the cosmetic and modifier roles the user has;
    String gender; //limit = 20 chars.
    String age; // limit = 20 chars.
    String shortBio; //limit to 140 chars.
    ArrayList<String> longBio = new ArrayList<>(); //can only be seen via a .txt File because of length limit of almost 2k.

    public CharacterObject(String name, String userID, String nickname, ArrayList<RoleTypeObject> roles) {
        this.name = name;
        this.userID = userID;
        this.nickname = nickname;
        this.roles = roles;
        gender = "Unknown";
        age = "Unknown";
        shortBio = "N/a";
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

    public void setLongBio(ArrayList<String> longBio) {
        this.longBio = longBio;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRoles(ArrayList<RoleTypeObject> roles) {
        this.roles = roles;
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

    public List<RoleTypeObject> getRoles() {
        return roles;
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

    public ArrayList<String> getLongBio() {
        return longBio;
    }
}
