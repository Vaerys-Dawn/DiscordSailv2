package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CompObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCompEntries implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        int i = 1;
        for (CompObject p : command.guild.competition.getEntries()) {
            Utility.sendMessage("Entry " + i + " : " + command.guild.get().getUserByID(p.getUserID()).mention() + "\n" +
                    p.getFileUrl(), command.channel.get());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
            i++;
        }
        return "";
    }

    @Override
    public String[] names() {
        return new String[]{"GetCompEntries"};
    }

    @Override
    public String description() {
        return "Posts all of the Competition Entries in the current channel.";
    }

    @Override
    public String usage() {
        return null;
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
