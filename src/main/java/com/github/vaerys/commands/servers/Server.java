package com.github.vaerys.commands.servers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ServerObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Server extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (ServerObject s : command.guild.servers.getServers()) {
            if (s.getName().equalsIgnoreCase(args)) {
                User user = command.guild.getUserByID(s.getCreatorID()).getUser();
                boolean isGuildUser = true;
                if (user == null) {
                    user = command.client.get().getUserById(s.getCreatorID());
                    isGuildUser = false;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("**" + s.getName() + "**\n");
                builder.append("**IP:** " + s.getServerIP() + " **Port:** " + s.getServerPort() + "\n");
                if (isGuildUser) {
                    builder.append("**Listing Creator:** " + command.guild.get().getMember(user).getNickname() + "\n");
                } else {
                    builder.append("**Listing Creator:** " + user.getName() + "#" + user.getDiscriminator() + "\n");
                }
                builder.append(s.getServerDesc());
                command.user.queueDm(builder.toString());
                return "\\> Server info has been sent to your DMs";
            }
        }
        return "\\> Server with that name not found.";
    }

    @Override
    protected String[] names() {
        return new String[]{"Server"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists the information about a specific server.";
    }

    @Override
    protected String usage() {
        return "[Server Name]";
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
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
