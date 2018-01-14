package com.github.vaerys.pogos;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.templates.GuildFile;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Vaerys on 14/08/2016.
 */

public class Characters extends GuildFile {
    public static final String FILE_PATH = "Characters.json";
    private double fileVersion = 1.0;
    ArrayList<CharacterObject> characters = new ArrayList<>();
    private String rolePrefix = "";

    public ArrayList<CharacterObject> getCharacters(IGuild guild) {
        validateRoles(guild);
        return characters;
    }

    public String delChar(String character, IUser author, IGuild guild, boolean bypass) {
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(character)) {
                if (author.getLongID() == c.getUserID() || bypass) {
                    characters.remove(c);
                    return "> Character Deleted.";
                } else {
                    return "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public String getRolePrefix() {
        return rolePrefix;
    }

    public void validateRoles(IGuild guild) {
        if (guild == null) {
            return;
        }
        for (CharacterObject c : characters) {
            ListIterator iterator = c.getRoleIDs().listIterator();
            while (iterator.hasNext()) {
                IRole role = guild.getRoleByID((long) iterator.next());
                if (role == null) {
                    iterator.remove();
                }
            }
        }
    }
}
