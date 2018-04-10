package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.ListIterator;

public class DenyXpPrefix extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("list")) {
            return "> Here are all of the prefixes that are registered.\n```" + Utility.listFormatter(command.guild.config.getXpDeniedPrefixes(), true) + "```";
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
        ListIterator<String> iterator = prefixes.listIterator();
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
    public String description(CommandObject command) {
        return "prefixes added to this list will stop commands starting with the prefix granting xp.";
    }

    @Override
    protected String[] names() {
        return new String[]{"DenyXpPrefix"};
    }

    @Override
    protected String usage() {
        return "[Prefix]/List";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
