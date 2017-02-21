package Commands.Admin;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {
            for (String channelType : command.channelTypes) {
                if (args.equalsIgnoreCase(channelType)) {
                    command.guildConfig.setUpChannel(channelType, command.channelID);
                    return "> This channel is now the Server's **" + channelType + "** channel.";
                }

            }
            builder.append("> Could not find channel type \"" + args + "\"\n");
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String title = "> Here is a list of available Channel Types:\n";
        ArrayList<String> channels = command.channelTypes;
        Collections.sort(channels);
        embedBuilder.withDesc(builder.toString());
        Utility.listFormatterEmbed(title, embedBuilder, channels, true);
        embedBuilder.appendField(spacer, Utility.getCommandInfo(this, command), false);
        embedBuilder.withColor(Utility.getUsersColour(command.client.getOurUser(), command.guild));
        Utility.sendEmbededMessage("", embedBuilder.build(), command.channel);
        return null;
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
        return "(Channel Type)";
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
