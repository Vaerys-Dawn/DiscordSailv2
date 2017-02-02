package Commands.Help;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import org.apache.commons.lang3.ArrayUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class Info implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<Command> commands = Globals.getCommands();

        for (Command c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {
                    EmbedBuilder infoEmbed = new EmbedBuilder();
                    Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
                    if (color != null) {
                        infoEmbed.withColor(color);
                    }

                    //command info
                    String primaryCommand = command.guildConfig.getPrefixCommand() + c.names()[0];
                    if (c.usage() != null){
                        primaryCommand += " " + c.usage();
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append("Desc: **" + c.description() + "**\n");
                    builder.append("Type: **" + c.type() + "**\n");
                    if (c.perms().length != 0) {
                        builder.append("Perms: **");
                        for (Permissions p : c.perms()) {
                            builder.append(p.name() + ", ");
                        }
                        builder.delete(builder.length() - 2, builder.length());
                        builder.append("**");
                    }
                    infoEmbed.appendField(primaryCommand, builder.toString(), false);

                    //dual command info
                    if (c.dualType() != null) {
                        String dualCommand = command.guildConfig.getPrefixCommand() + c.names()[0];
                        if (c.dualUsage() != null){
                            dualCommand += " " + c.dualUsage();
                        }
                        StringBuilder builderDual = new StringBuilder();
                        builderDual.append("Desc: **" + c.dualDescription() + "**\n");
                        builderDual.append("Type: **" + c.dualType() + "**\n");
                        if (c.perms().length != 0 || c.dualPerms().length != 0) {
                            Permissions[] perms;
                            perms = ArrayUtils.addAll(c.dualPerms(), c.perms());
                            builderDual.append("Perms: **");
                            for (Permissions p : perms) {
                                builderDual.append(p.name() + ", ");
                            }
                            builderDual.delete(builderDual.length() - 2, builderDual.length());
                            builderDual.append("**");
                        }
                        infoEmbed.appendField(dualCommand, builderDual.toString(), false);
                    }
                    IChannel commandChannel = command.client.getChannelByID(command.guildConfig.getChannelTypeID(c.channel()));
                    //channel
                    if (commandChannel != null) {
                        infoEmbed.appendField("Channel: ", commandChannel.mention(), false);
                    }

                    //aliases
                    if (c.names().length > 1) {
                        StringBuilder aliasBuilder = new StringBuilder();
                        for (int i = 1;i < c.names().length;i++) {
                            aliasBuilder.append(command.guildConfig.getPrefixCommand() + c.names()[i] + ", ");
                        }
                        aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
                        aliasBuilder.append(".\n");
                        infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
                    }
                    Utility.sendEmbededMessage("", infoEmbed.build(), command.channel);
                    return "";
                }
            }
        }
        return "> Command with the name " + args + " not found.";
    }

    @Override
    public String[] names() {
        return new String[]{"Info"};
    }

    @Override
    public String description() {
        return "Gives information about a command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
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
