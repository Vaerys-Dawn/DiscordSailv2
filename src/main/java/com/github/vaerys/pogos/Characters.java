package com.github.vaerys.pogos;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CharacterObject;
import com.github.vaerys.objects.userlevel.DungeonCharObject;
import com.github.vaerys.templates.GlobalFile;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 14/08/2016.
 */

public class Characters extends GlobalFile {
    public static final String FILE_PATH = "Characters.json";
    private double fileVersion = 1.0;
    ArrayList<CharacterObject> characters = new ArrayList<>();
    List<DungeonCharObject> dungeonChars = new ArrayList<>();
    private String rolePrefix = "";

    public ArrayList<CharacterObject> getCharacters(Guild guild) {
        validateRoles(guild);
        return characters;
    }

    public String delChar(String character, Member author, boolean bypass) {
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(character)) {
                if (author.getIdLong() == c.getUserID() || bypass) {
                    characters.remove(c);
                    return "\\> Character Deleted.";
                } else {
                    return "\\> I'm sorry " + author.getEffectiveName() + ", I'm afraid I can't do that.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }

    public String getRolePrefix() {
        return rolePrefix;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void validateRoles(Guild guild) {
        if (guild == null) {
            return;
        }
        for (CharacterObject c : characters) {
            ListIterator iterator = c.getRoleIDs().listIterator();
            while (iterator.hasNext()) {
                Role role = guild.getRoleById((long) iterator.next());
                if (role == null) {
                    iterator.remove();
                }
            }
        }
    }

    public void updateVars(Guild guild) {
        for (CharacterObject c : characters) {
            ListIterator iterator = c.getRoleIDs().listIterator();
            while (iterator.hasNext()) {
                long id = (long) iterator.next();
                if (guild.getRoleById(id) == null) {
                    iterator.remove();
                }
            }
        }
    }

    public CharacterObject getCharByName(String args) {
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(args)) {
                return c;
            }
        }
        return null;
    }

    public List<CharacterObject> getCharactersForUser(long id) {
        return characters.stream().filter(c->c.getUserID()==id).collect(Collectors.toList());
    }

    public List<CharacterObject> getCharactersForUser(UserObject user) {
        return getCharactersForUser(user.longID);
    }

    public int maxCharsForUser(UserObject user, GuildObject guild) {
        int maxChars = 4;
        int rewardCount = PixelHandler.getRewardCount(guild, user.longID);
        maxChars += rewardCount * 2;
        if (user.isPatron) maxChars += 4;
        if (GuildHandler.canBypass(user, guild)) maxChars += 4;
        if (GuildHandler.testForPerms(user, guild, Permission.MESSAGE_MANAGE)) maxChars += 4;
        return maxChars;
    }

    public int maxCharsForUser(CommandObject command) {
        return maxCharsForUser(command.user, command.guild);
    }

    public void addChar(String characterID, List<Role> roles, UserObject user) {
        characters.add(new CharacterObject(characterID, user.longID, user.displayName, roles.stream().map(iRole -> iRole.getIdLong()).collect(Collectors.toList())));
    }


    public void addDungeonChar(String test) {
        dungeonChars.add(new DungeonCharObject(test));
    }

    public boolean checkForUser(long userID) {
        if (characters.stream().map(c -> c.getUserID()).filter(c -> c == userID).toArray().length != 0) return true;
        return false;
    }
}
