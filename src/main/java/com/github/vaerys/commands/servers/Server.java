package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.ServerObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Server extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (ServerObject s : command.guild.servers.getServers()) {
            if (s.getName().equalsIgnoreCase(args)) {
                IUser user = command.guild.getUserByID(s.getCreatorID());
                boolean isGuildUser = true;
                if (user == null) {
                    user = command.client.get().fetchUser(s.getCreatorID());
                    isGuildUser = false;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("**" + s.getName() + "**\n");
                builder.append("**IP:** " + s.getServerIP() + " **Port:** " + s.getServerPort() + "\n");
                if (isGuildUser) {
                    builder.append("**Listing Creator:** " + user.getDisplayName(command.guild.get()) + "\n");
                } else {
                    builder.append("**Listing Creator:** " + user.getName() + "#" + user.getDiscriminator() + "\n");
                }
                builder.append(s.getServerDesc());
                command.user.sendDm(builder.toString());
                return "> Server info has been sent to your DMs";
            }
        }
        return "> Server with that name not found.";
    }

    protected static final String[] NAMES = new String[]{"Server"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Lists the information about a specific server.";
    }

    protected static final String USAGE = "[Server Name]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.SERVERS;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.SERVERS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
