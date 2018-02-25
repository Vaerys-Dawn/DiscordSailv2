package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
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
                builder.withColor(GuildHandler.getUsersColour(user, command.guild.get()));
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
                Utility.sendStack(e);
            }
        }
        return "";
    }

    protected static final String[] NAMES = new String[]{"GetCompEntries"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Posts all of the Competition Entries in the current channel.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.COMPETITION;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
