package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class SelectChar implements Command {

    private final static Logger logger = LoggerFactory.getLogger(SelectChar.class);

    @Override
    public String execute(String args, CommandObject command) {
        for (CharacterObject c : command.guild.characters.getCharacters(command.guild.get())) {
            if (c.getName().equalsIgnoreCase(args)) {
                if (c.getUserID() == command.user.longID) {
                    List<IRole> userRoles = command.guild.get().getRolesForUser(command.user.get());
                    //resets User roles back to scratch.
                    for (int i = 0; i < userRoles.size(); i++) {
                        if (command.guild.config.isRoleCosmetic(userRoles.get(i).getLongID())) {
                            userRoles.remove(i);
                        }
                    }
                    //loads new roles.
                    for (long r : c.getRoleIDs()) {
                        userRoles.add(command.guild.get().getRoleByID(r));
                    }
                    Utility.roleManagement(command.user.get(), command.guild.get(), userRoles);
                    Utility.updateUserNickName(command.user.get(), command.guild.get(), c.getNickname());
                    return "> Character " + c.getNickname() + " Loaded.";
                } else {
                    return "> " + c.getName() + " is not your character.";
                }
            }
        }
        return Constants.ERROR_CHAR_NOT_FOUND;
    }

    @Override
    public String[] names() {
        return new String[]{"Char", "SelChar", "SelectChar"};
    }

    @Override
    public String description() {
        return "Selects a Character.";
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
