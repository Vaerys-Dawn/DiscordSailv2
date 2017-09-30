package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class HelpTags implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return "> https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide";
    }

    @Override
    public String[] names() {
        return new String[]{"HelpTags"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about tags that you can use with S.A.I.L.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
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
