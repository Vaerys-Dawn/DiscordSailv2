package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CompObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCompEntries extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.competition.getEntries().size() == 0) {
            return "> No entries were found.";
        }
        List<CompObject> compObjects = command.guild.competition.getEntries();
        for (int i = 0; i < compObjects.size(); i++) {
            XEmbedBuilder builder = new XEmbedBuilder(command);
            builder.withTitle("Entry " + (i + 1));
            IUser user = command.guild.getUserByID(compObjects.get(i).getUserID());
            if (user != null) {
                builder.withDesc(user.mention());
                builder.withColor(Utility.getUsersColour(user, command.guild.get()));
            }
            if (Utility.isImageLink(compObjects.get(i).getFileUrl())) {
                builder.withThumbnail(compObjects.get(i).getFileUrl());
            } else {
                if (user != null) {
                    builder.withDesc(user.mention() + "\n" + compObjects.get(i).getFileUrl());
                } else {
                    builder.withDesc(compObjects.get(i).getFileUrl());
                }
            }
            builder.send(command.channel);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public String[] names() {
        return new String[]{"GetCompEntries"};
    }

    @Override
    public String description(CommandObject command) {
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
    public void init() {

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
