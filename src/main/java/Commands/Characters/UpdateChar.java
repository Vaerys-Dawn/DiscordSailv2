package Commands.Characters;

import Commands.CommandObject;
import Interfaces.Command;
import Objects.CharacterObject;
import Objects.RoleTypeObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateChar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<RoleTypeObject> charRoles = new ArrayList<>();
        for (IRole r : command.authorRoles) {
            for (RoleTypeObject ro : command.guildConfig.getCosmeticRoles()) {
                if (r.getStringID().equals(ro.getRoleID())) {
                    charRoles.add(ro);
                }
            }
        }
        return command.characters.updateChar(new CharacterObject(args.split(" ")[0], command.authorSID, command.authorDisplayName, charRoles));
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateChar","NewChar"};
    }

    @Override
    public String description() {
        return "Updates/Creates a Character.";
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
