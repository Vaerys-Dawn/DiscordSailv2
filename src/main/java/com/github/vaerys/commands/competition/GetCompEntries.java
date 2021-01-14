package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.CompObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class GetCompEntries extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.competition.getEntries().size() == 0) {
            return "\\> No entries were found.";
        }
        List<CompObject> compObjects = command.guild.competition.getEntries();
        for (int i = 0; i < compObjects.size(); i++) {
            XEmbedBuilder builder = new XEmbedBuilder(command);
            builder.setTitle("Entry " + (i + 1));
            Member user = command.guild.getUserByID(compObjects.get(i).getUserID());
            if (user != null) {
                builder.setDescription(user.getAsMention());
                builder.setColor(GuildHandler.getUsersColour(user, command.guild.get()));
            }
            if (Utility.isImageLink(compObjects.get(i).getFileUrl())) {
                builder.setThumbnail(compObjects.get(i).getFileUrl());
            } else {
                if (user != null) {
                    builder.setDescription(user.getAsMention() + "\n" + compObjects.get(i).getFileUrl());
                } else {
                    builder.setDescription(compObjects.get(i).getFileUrl());
                }
            }
            builder.queue(command.guildChannel);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
        }
        return "";
    }

    @Override
    protected String[] names() {
        return new String[]{"GetCompEntries"};
    }

    @Override
    public String description(CommandObject command) {
        return "Posts all of the Competition Entries in the current messageChannel.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.COMPETITION;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
