package com.github.vaerys.commands.pixels;

import java.util.ArrayList;
import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

public class DenyXpPrefix extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[] {"DenyXpPrefix"};
    protected static final String USAGE = "[Prefix]/List";
    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[] {Permissions.MANAGE_SERVER};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = true;

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
    public void init() {

    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }
}
