package Commands.Characters;

import Handlers.MessageHandler;
import Interfaces.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Utility;
import Objects.CharacterObject;
import Objects.RoleTypeObject;
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
        for (CharacterObject c : command.characters.getCharacters()) {
            if (c.getName().equalsIgnoreCase(args)) {
                if (c.getUserID().equals(command.authorID)) {
                    List<IRole> userRoles = command.guild.getRolesForUser(command.author);
                    int roleCount = 0;
                    int postRoleCount = 0;
                    //resets User roles back to scratch.
                    for (int i = 0; i < userRoles.size(); i++) {
                        if (command.guildConfig.isRoleCosmetic(userRoles.get(i).getID())) {
                            userRoles.remove(i);
                        }
                    }
                    //loads new roles.
                    for (RoleTypeObject r : c.getRoles()) {
                        userRoles.add(command.guild.getRoleByID(r.getRoleID()));
                    }
                    Utility.roleManagement(command.author, command.guild, userRoles);
                    Utility.updateUserNickName(command.author, command.guild, c.getNickname());
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
