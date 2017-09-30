package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/06/2017.
 */
public class SetBioRolePrefix implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()){
            command.guild.characters.setRolePrefix("");
            return "> Role Prefix Removed.";
        }
        if (args.length() < 20) {
            command.guild.characters.setRolePrefix(args);
            return "> Role Prefix Updated.";
        }else {
            return "> Role Prefix is too long.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetCharRolePrefix"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set what shows before the roles of a character.";
    }

    @Override
    public String usage() {
        return "(Prefix)";
    }

    @Override
    public String type() {
        return TYPE_CHARACTER;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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
