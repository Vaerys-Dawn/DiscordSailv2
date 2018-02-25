package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ServerObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers extends Command {

    protected static final String[] NAMES = new String[]{"ListServers", "Servers", "ServerList"};
    protected static final String USAGE = null;
    protected static final SAILType COMMAND_TYPE = SAILType.SERVERS;
    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.SERVERS;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        String title = "> Here are the Servers I have Listed:";
        ArrayList<String> serverNames = command.guild.servers.getServers().stream().map(ServerObject::getName).collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(serverNames);
        String suffix = Utility.getCommandInfo(new Server(), command);
        Utility.listFormatterEmbed(title, builder, serverNames, false, suffix);
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Shows a listing of this guild's user registered servers.";
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

    @Override
    public void init() {

    }
}
