package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.Field;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {
            try {
                for (Field f : Command.class.getDeclaredFields()) {
                    if (f.getName().contains("CHANNEL_") && f.getType() == String.class) {
                        if (args.equalsIgnoreCase((String) f.get(null))) {
                            command.guildConfig.setUpChannel((String) f.get(null), command.channelID);
                            return "> This channel is now the Server's **" + f.get(null) + "** channel.";
                        }
                    }
                }
                builder.append("> Could not find channel type \"" + args + "\"\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        builder.append("> Here is a list of available channel types:\n`");
        try {
            for (Field f : Command.class.getDeclaredFields()) {
                if (f.getName().contains("CHANNEL_") && f.getType() == String.class) {
                    builder.append(f.get(null) + ", ");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("`.");
        return builder.toString();
    }

    @Override
    public String[] names() {
        return new String[]{"ChannelHere", "SetupChannel"};
    }

    @Override
    public String description() {
        return "Sets the current channel as the channel type you select.";
    }

    @Override
    public String usage() {
        return "[Channel Type]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
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
