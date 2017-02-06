package POGOs;

import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.CharacterObject;
import Objects.RoleTypeObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 14/08/2016.
 */

public class Characters {

    // TODO: 22/09/2016 add more profile options: link roles, display name, gender, age, bio.
    boolean properlyInit = false;
    ArrayList<CharacterObject> characters = new ArrayList<>();


    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public String updateChar(CharacterObject newCharacter) {
        int position = 0;
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(newCharacter.getName())) {
                IUser author = Globals.getClient().getUserByID(newCharacter.getUserID());
                if (c.getUserID().equals(author.getID())) {
                    characters.set(position, newCharacter);
                    return "> Character Updated.";
                } else {
                    return "> " + author.getName() + "#" + author.getDiscriminator() + "'s Character: \"" + newCharacter.getName() + "\" Could not be added as that name is already in use.";
                }
            }
            position++;
        }
        characters.add(newCharacter);
        return "> Character: \"" + newCharacter.getName() + "\" Added";
    }

    public String selChar(String character, IUser author, IGuild guild, GuildConfig guildConfig) {
        List<IRole> userRoles = author.getRolesForGuild(guild);
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(character)) {
                if (author.getID().equals(c.getUserID())) {
                    for (RoleTypeObject r : guildConfig.getCosmeticRoles()) {
                        for (int i = 0; i < userRoles.size(); i++) {
                            if (r.getRoleID().equalsIgnoreCase(userRoles.get(i).getID())) {
                                userRoles.remove(i);
                            }
                        }

                    }
                    for (int i = 0; i < c.getRoles().size(); i++) {

                        IRole newRole = guild.getRoleByID(c.getRoles().get(i).getRoleID());
                        if (newRole != null) {
                            userRoles.add(newRole);
                        } /*else {
                        c.getRoles().remove(i);
                    }*/

                    }
                    Utility.roleManagement(author, guild, userRoles);
                    Utility.updateUserNickName(author, guild, c.getNickname());
                    return "> Loaded Character.";
                } else {
                    return "> That is not your character.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }

    public ArrayList<CharacterObject> getCharacters() {
        return characters;
    }

    public String delChar(String character, IUser author, IGuild guild, boolean bypass) {
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(character)) {
                if (author.getID().equals(c.getUserID()) || bypass) {
                    characters.remove(c);
                    return "> Character Deleted.";
                } else {
                    return "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }
}
