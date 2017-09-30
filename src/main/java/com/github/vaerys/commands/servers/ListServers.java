package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ServerObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        String title = "> Here are the Servers I have Listed:";
        ArrayList<String> serverNames = command.guild.servers.getServers().stream().map(ServerObject::getName).collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(serverNames);
        String suffix = Utility.getCommandInfo(new Server(), command);
        Utility.listFormatterEmbed(title,builder,serverNames,false,suffix);
        builder.withColor(Utility.getUsersColour(command.client.bot, command.guild.get()));
        Utility.sendEmbedMessage("",builder,command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListServers","Servers","ServerList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows a listing of this guild's user registered servers.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_SERVERS;
    }

    @Override
    public String channel() {
        return CHANNEL_SERVERS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
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
