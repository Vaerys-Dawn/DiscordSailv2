package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.objects.CharacterObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateChar implements Command {
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
        return command.guild.characters.updateChar(new CharacterObject(args.split(" ")[0], command.user.longID, command.user.displayName, charRoles), command.guild.get());
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateChar", "NewChar"};
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
