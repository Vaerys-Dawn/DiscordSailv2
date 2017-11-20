package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelChar implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        boolean bypass = false;
        if (Utility.testForPerms(command,Permissions.MANAGE_MESSAGES)) {
            bypass = true;
        }
        return command.guild.characters.delChar(args.split(" ")[0], command.user.get(), command.guild.get(), bypass);
    }

    @Override
    public String[] names() {
        return new String[]{"DelChar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Deletes a Character.";
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
