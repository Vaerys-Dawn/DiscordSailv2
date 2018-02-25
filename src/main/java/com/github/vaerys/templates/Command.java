package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */
public abstract class Command {

    public static final String spacer = "\u200B";
    public static final String indent = "    ";
    public static final String codeBlock = "```";
    public static final String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";


    public final ChannelSetting channel;
    public final SAILType type;
    public final String[] names;
    public final String usage;
    public final Permissions[] perms;
    public final boolean requiresArgs;
    public final boolean doAdminLogging;


    public Command() {
        this.type = type();
        this.channel = channel();
        this.names = names();
        this.usage = usage();
        this.perms = perms();
        this.requiresArgs = requiresArgs();
        this.doAdminLogging = doAdminLogging();
        init();
    }


    public List<SubCommandObject> subCommands = new LinkedList<>();

    /**
     * The code to be executed when the command is ran
     *
     * @param args    - The args passed to the command
     * @param command - The command object to getToggles data about where the command was sent from
     * @return The text or data to send back for the command
     */
    public abstract String execute(String args, CommandObject command);


    /**
     * Gets the list of names that are associated with the command
     *
     * @return the list of names associated with he command
     */
    protected abstract String[] names();

    /**
     * The description of the command
     *
     * @return the description of the command
     */
    public abstract String description(CommandObject command);

    /**
     * Gets the usage of the command
     *
     * @return the usage of the command
     */
    protected abstract String usage();

    /**
     * Gets the command type
     *
     * @return the command type
     */
    protected abstract SAILType type();

    /**
     * The channel type the command can be ran in
     *
     * @return the type of channel it can be ran in
     */
    protected abstract ChannelSetting channel();

    protected abstract Permissions[] perms();

    protected abstract boolean requiresArgs();

    protected abstract boolean doAdminLogging();

    protected abstract void init();

    public String getCommand(CommandObject command) {
        return command.guild.config.getPrefixCommand() + names[0];
    }

    public String getCommand(CommandObject command, int i) {
        return command.guild.config.getPrefixCommand() + names[i];
    }

    public String getUsage(CommandObject command) {
        if (usage() == null || usage().isEmpty()) {
            return getCommand(command);
        } else {
            return getCommand(command) + " " + usage();
        }
    }

    public String missingArgs(CommandObject command) {
        return ">> **" + getUsage(command) + "** <<";
    }

    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        for (String s : names) {
            if ((command.guild.config.getPrefixCommand() + s).equalsIgnoreCase(call.getFirstWord())) {
                return true;
            }
        }
        return false;
    }

    public String getArgs(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    /**
     * Creates a message used to fetch the command's documentations
     *
     * @param command
     * @return
     */
    public XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder infoEmbed = new XEmbedBuilder(command);

        //command info
        StringBuilder builder = new StringBuilder();
        builder.append(description(command) + "\n");
        builder.append("**Type: **" + type.toString() + ".");


        // display permissions
        if (perms != null && perms.length != 0) {

            builder.append("\n**Perms: **");
            ArrayList<String> permList = new ArrayList<>(perms.length);
            for (Permissions p : perms) {
                permList.add(p.toString());
            }
            builder.append(Utility.listFormatter(permList, true));
        }

        if (names.length > 1) {
            List<String> aliases = Arrays.asList(names).stream().map(s -> command.guild.config.getPrefixCommand() + s).collect(Collectors.toList());
            aliases.remove(0);
            builder.append("\n**Aliases:** " + Utility.listFormatter(aliases, true));
        }

        if (subCommands.size() != 0) builder.append("\n" + Command.spacer);

        infoEmbed.withTitle("> Help - " + names()[0]);
        infoEmbed.appendField("**" + getUsage(command) + "**", builder.toString(), true);


        for (SubCommandObject s : subCommands) {
            infoEmbed.appendField(s.getCommandUsage(command), s.getHelpDesc(command), true);
        }

        //Handle channels
        List<IChannel> channels = command.guild.getChannelsByType(channel);
        List<String> channelMentions = Utility.getChannelMentions(channels);

        //channel
        if (channelMentions.size() > 0) {
            if (channelMentions.size() == 1) {
                infoEmbed.appendField("Channel ", Utility.listFormatter(channelMentions, true), false);
            } else {
                infoEmbed.appendField("Channels ", Utility.listFormatter(channelMentions, true), false);
            }
        }

        //aliases

        return infoEmbed;
    }


    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isError = false;
        response.append(Utility.formatError(this));
        if (names.length == 0 || names[0].isEmpty()) {
            response.append("   > Command name is empty.\n");
            isError = true;
        }
        if (description(new CommandObject()) == null || description(new CommandObject()).isEmpty()) {
            response.append("   > Command description is empty.\n");
            isError = true;
        }
        if (type == null) {
            response.append("   > Command type is empty.\n");
            isError = true;
        }
        if (requiresArgs && (usage == null || usage.isEmpty())) {
            response.append("   > Command usage is null when requiresArgs is true.\n");
            isError = true;
        }
        if (isError) {
            return response.toString();
        } else {
            return null;
        }
    }

    public boolean isSubtype(CommandObject command, String subType) {
        return command.message.get().getContent().toLowerCase().startsWith(command.guild.config.getPrefixCommand() + subType.toLowerCase());
    }
}
