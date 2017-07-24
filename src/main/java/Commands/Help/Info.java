package Commands.Help;

import Commands.CommandObject;
import Interfaces.ChannelSetting;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.UserLinkObject;
import Objects.XEmbedBuilder;
import org.apache.commons.lang3.ArrayUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class Info implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<Command> commands = command.commands;

        String error = "> Could not find information on any commands named **" + args + "**.";
        for (Command c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {
                    if (c.type().equalsIgnoreCase(TYPE_CREATOR) && !command.authorSID.equalsIgnoreCase(Globals.creatorID)) {
                        return error;
                    }
                    if (!Utility.testForPerms(c.perms(),command.author,command.guild)){
                        return error;
                    }

                    XEmbedBuilder infoEmbed = new XEmbedBuilder();
                    Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
                    if (color != null) {
                        infoEmbed.withColor(color);
                    }

                    //command info
                    StringBuilder builder = new StringBuilder();
                    String primaryCommand = command.guildConfig.getPrefixCommand() + c.names()[0];
                    if (c.usage() != null) {
                        primaryCommand += " " + c.usage();
                    }
                    builder.append("**" + primaryCommand + "**\n");
                    builder.append("**Desc: **" + c.description() + "\n");
                    builder.append("**Type: **" + c.type() + "\n");
                    if (c.perms().length != 0) {
                        builder.append("**Perms: **");
                        ArrayList<String> permList = new ArrayList<>();
                        for (Permissions p : c.perms()) {
                            permList.add(p.toString());
                        }
                        builder.append(Utility.listFormatter(permList, true));
                    }
                    //dual command info
                    if (c.dualType() != null && Utility.testForPerms(c.dualPerms(), command.author, command.guild)) {
                        String dualCommand = command.guildConfig.getPrefixCommand() + c.names()[0];
                        if (c.dualUsage() != null) {
                            dualCommand += " " + c.dualUsage();
                        }
                        StringBuilder builderDual = new StringBuilder();
                        builder.append("\n**" + dualCommand + "**\n");
                        builder.append("**Desc: **" + c.dualDescription() + "\n");
                        builder.append("**Type: **" + c.dualType() + "\n");
                        if (c.perms().length != 0 || c.dualPerms().length != 0) {
                            Permissions[] perms;
                            perms = ArrayUtils.addAll(c.dualPerms(), c.perms());
                            builder.append("**Perms: **");
                            ArrayList<String> permList = new ArrayList<>();
                            for (Permissions p : perms) {
                                permList.add(p.toString());
                            }
                            builder.append(Utility.listFormatter(permList, true));
                        }
                        builder.append(builderDual.toString());
                    }
                    infoEmbed.appendField("> Info - " + c.names()[0], builder.toString(), false);

                    //Handle channels
                    ArrayList<String> channelIDs = command.guildConfig.getChannelIDsByType(c.channel());
                    ArrayList<String> channelMentions = new ArrayList<>();

                    if (channelIDs != null) {
                        for (String id: channelIDs){
                            IChannel temp = command.guild.getChannelByID(id);
                            if (temp != null){
                                EnumSet<Permissions> userPerms = temp.getModifiedPermissions(command.author);
                                if (userPerms.contains(Permissions.SEND_MESSAGES) && userPerms.contains(Permissions.READ_MESSAGES)) {
                                    channelMentions.add(temp.mention());
                                }
                            }
                        }
                        builder.append("\n" + Utility.listFormatter(channelMentions, true));
                    }

                    //channel
                    if (channelMentions.size() > 0) {
                        if (channelMentions.size() == 1) {
                            infoEmbed.appendField("Channel ", Utility.listFormatter(channelMentions, true), false);
                        } else {
                            infoEmbed.appendField("Channels ", Utility.listFormatter(channelMentions, true), false);
                        }
                    }

                    //aliases
                    if (c.names().length > 1) {
                        StringBuilder aliasBuilder = new StringBuilder();
                        for (int i = 1; i < c.names().length; i++) {
                            aliasBuilder.append(command.guildConfig.getPrefixCommand() + c.names()[i] + ", ");
                        }
                        aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
                        aliasBuilder.append(".\n");
                        infoEmbed.appendField("Aliases", aliasBuilder.toString(), false);
                    }
                    Utility.sendEmbedMessage("", infoEmbed, command.channel);
                    return "";
                }
            }

        }
        return error;
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
        return true;
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
