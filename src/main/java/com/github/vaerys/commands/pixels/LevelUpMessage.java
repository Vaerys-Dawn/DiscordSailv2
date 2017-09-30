package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class LevelUpMessage implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (args.length() > 100) {
            return "> Message to long.";
        } else {
            if (!args.contains("<level>") || !args.contains("<user>")) {
                return "> Missing <level> and/or <user> tags.";
            }
            command.guild.config.setLevelUpMessage(args);
            return "> New Level Up message set.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetLvlMessage"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the level up message for the server.\n" +
                "**Available Tags:**" +
                "<level>, <user>, <randEmote>.";
    }

    @Override
    public String usage() {
        return "[Message]";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
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
