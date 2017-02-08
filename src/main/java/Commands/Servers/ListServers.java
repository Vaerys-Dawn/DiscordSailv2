package Commands.Servers;

import Commands.Command;
import Commands.CommandObject;
import Main.Utility;
import Objects.ServerObject;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        EmbedBuilder builder = new EmbedBuilder();
        String title = "> Here are the Servers I have Listed:";
        ArrayList<String> serverNames = command.servers.getServers().stream().map(ServerObject::getName).collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(serverNames);
        String suffix = Utility.getCommandInfo(new Server(), command);
        Utility.listFormatterEmbed(title,builder,serverNames,false,suffix);
        builder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        Utility.sendEmbededMessage("",builder.build(),command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ListServers","Servers","ServerList"};
    }

    @Override
    public String description() {
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
