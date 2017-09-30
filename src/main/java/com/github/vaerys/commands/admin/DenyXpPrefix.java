package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.ListIterator;

public class DenyXpPrefix implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("list")) {
            return "> Here are all of the prefixes that are registered.\n```" +
                    Utility.listFormatter(command.guild.config.getXpDeniedPrefixes(), true) + "```";
        }
        boolean isAlphanumeric = args.matches("[A-Za-z0-9]+");
        if (args.length() > 5) {
            return "> Prefix cannot be longer than 5 chars.";
        }
        if (args.contains("\n")) {
            return "> Prefix cannot contain newlines.";
        }
        if (args.contains(" ")) {
            return "> Prefix cannot contain spaces";
        }
        if (isAlphanumeric) {
            return "> Prefix cannot be alphanumeric.";
        }
        if (args.startsWith("#")) {
            return "> Prefix cannot start with a `#`.";
        }
        if (args.startsWith("@")) {
            return "> Prefix cannot start with a `@`.";
        }
        ArrayList<String> prefixes = command.guild.config.getXpDeniedPrefixes();
        ListIterator iterator = prefixes.listIterator();
        while (iterator.hasNext()) {
            String prefix = (String) iterator.next();
            if (prefix.equals(args)) {
                iterator.remove();
                return "> Removed the **" + prefix + "** prefix from the xp denied prefixes.";
            }
        }
        prefixes.add(args);
        return "> Added the **" + args + "** prefix to the list of xp denied prefixes.";
    }

    @Override
    public String[] names() {
        return new String[]{"DenyXpPrefix"};
    }

    @Override
    public String description(CommandObject command) {
        return "prefixes added to this list will stop commands starting with the prefix granting xp.";
    }

    @Override
    public String usage() {
        return "[Prefix]/List";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
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
        return true;
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