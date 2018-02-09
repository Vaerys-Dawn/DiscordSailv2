package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.CharacterObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateChar extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<Long> charRoles = new ArrayList<>();
        for (IRole r : command.user.roles) {
            for (long ro : command.guild.config.getCosmeticRoleIDs()) {
                if (r.getLongID() == ro) {
                    charRoles.add(ro);
                }
            }
        }
        command.guild.characters.validateRoles(command.guild.get());
        int position = 0;
        CharacterObject newCharacter = new CharacterObject(args.split(" ")[0], command.user.longID, command.user.displayName, charRoles);
        List<CharacterObject> characters = command.guild.characters.getCharacters(command.guild.get());
        for (CharacterObject c : characters) {
            if (c.getName().equalsIgnoreCase(newCharacter.getName())) {
                IUser author = Globals.getClient().getUserByID(newCharacter.getUserID());
                if (c.getUserID() == author.getLongID()) {
                    characters.get(position).update(newCharacter);
                    return "> Character Updated.";
                } else {
                    return "> " + author.getName() + "#" + author.getDiscriminator() + "'s Character: \"" + newCharacter.getName() + "\" Could not be added as that name is already in use.";
                }
            }
            position++;
        }
        characters.add(newCharacter);
        if (isSubtype(command, names()[0])) {
            return "> Character: \"" + newCharacter.getName() + "\" Added.\n\n" +
                    "To update the name or roles linked to this character just run this command again.";
        } else {
            return "> Character: \"" + newCharacter.getName() + "\" Added.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"NewChar", "UpdateChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Updates/Creates or loads the character with the data from your discord account into the character. (Cosmetic roles and Nickname)";
    }

    @Override
    public String usage() {
        return "[Character Name]";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
