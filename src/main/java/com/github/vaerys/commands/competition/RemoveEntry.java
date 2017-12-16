package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CompObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class RemoveEntry implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        try {
            int entry = Integer.parseInt(args) - 1;
            CompObject didRemove = command.guild.competition.getEntries().remove(entry);
            return "> Removed entry " + (entry + 1) + "\n<" + didRemove.getFileUrl() + ">";
        } catch (NumberFormatException e) {
            return "> Needs a valid number";
        } catch (IndexOutOfBoundsException e) {
            return "> Could not find entry";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"RemoveEntry"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a entry";
    }

    @Override
    public String usage() {
        return "[Entry Number]";
    }

    @Override
    public String type() {
        return TYPE_COMPETITION;
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