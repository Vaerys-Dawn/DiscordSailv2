package com.github.vaerys.commands.servers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ServerObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.servers.getServers().size() == 0) {
            return "\\> No servers have been listed yet, If you would like to list one yourself you can do so using **" +
                    get(AddServer.class).getUsage(command) + "**.";
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        String title = "\\> Here are the Servers I have Listed:";
        ArrayList<String> serverNames = command.guild.servers.getServers().stream().map(ServerObject::getName).collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(serverNames);
        String suffix = get(Server.class).missingArgs(command);
        Utility.listFormatterEmbed(title, builder, serverNames, false, suffix);
        command.guildChannel.queueMessage(builder.build());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"ListServers", "Servers", "ServerList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows a listing of this guild's globalUser registered servers.";
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
    protected Permission[] perms() {
        return new Permission[0];
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
