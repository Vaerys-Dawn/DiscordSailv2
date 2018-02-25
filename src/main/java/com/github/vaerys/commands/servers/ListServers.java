package com.github.vaerys.commands.servers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ServerObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers extends Command {

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
        return new String[]{"ListServers", "Servers", "ServerList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows a listing of this guild's user registered servers.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.SERVERS;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.SERVERS;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
