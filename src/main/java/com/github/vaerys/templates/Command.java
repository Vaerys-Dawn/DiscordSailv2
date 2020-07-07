package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */
public abstract class Command {

    public static final String spacer = "\u200B";
    public static final String indent = "    ";
    public static final String codeBlock = "```";
    public static final String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";

    private final static Logger logger = LoggerFactory.getLogger(Command.class);

    public final ChannelSetting channel;
    public final SAILType type;
    public final String[] names;
    public final String usage;
    public final Permission[] perms;
    public final boolean requiresArgs;
    public final boolean doAdminLogging;
    public List<SubCommandObject> subCommands = new LinkedList<>();
    public boolean showIndividualSubs = false;


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

    /**
     * The code to be executed when the command is ran
     *
     * @param args    - The args passed to the command
     * @param command - The command object to get data about where the command was sent from
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

    protected abstract Permission[] perms();

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
        List<String> validStates = new ArrayList(Arrays.asList(names));
        subCommands.forEach(subCommandObject -> {
            for (String s : subCommandObject.getNames()) {
                validStates.add(s + subCommandObject.getRegex());
            }
        });
        if (args.length() > 200) {
            args = StringUtils.truncate(args, 200);
        }
        for (String s : validStates) {
            String regexString = "^(?i)" + Utility.escapeRegex(command.guild.config.getPrefixCommand()) + s + " (.|\n)*";
            String regexStringEnd = "^(?i)" + Utility.escapeRegex(command.guild.config.getPrefixCommand()) + s + "$";
            if (Pattern.compile(regexString).matcher(args).matches() || Pattern.compile(regexStringEnd).matcher(args).matches()) {
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
            for (Permission p : perms) {
                permList.add(Utility.enumToString(p));
            }
            builder.append(Utility.listFormatter(permList, true));
        }

        if (names.length > 1) {
            List<String> aliases = Arrays.asList(names).stream().map(s -> command.guild.config.getPrefixCommand() + s).collect(Collectors.toList());
            aliases.remove(0);
            builder.append("\n**Aliases:** " + Utility.listFormatter(aliases, true));
        }

        List<SubCommandObject> objectList = subCommands.stream()
                .filter(subCommandObject -> GuildHandler.testForPerms(command, subCommandObject.getPermissions()))
                .collect(Collectors.toList());

        if (objectList.size() != 0) builder.append("\n" + Command.spacer);

        infoEmbed.setTitle("> Help - " + names()[0]);
        infoEmbed.addField("**" + getUsage(command) + "**    " + Command.spacer, builder.toString(), true);


        for (SubCommandObject s : objectList) {
            infoEmbed.addField(s.getCommandUsage(command) + "    " + Command.spacer, s.getHelpDesc(command), true);
        }

        //Handle channels
        List<IChannel> channels = command.guild.getChannelsByType(channel);
        List<String> channelMentions = command.user.getVisibleChannels(channels).stream().map(c -> c.mention()).collect(Collectors.toList());

        //channel
        if (channels.size() > 0) {
            if (channelMentions.size() != 0) {
                infoEmbed.addField(channels.size() == 1 ? "Channel " : "Channels", Utility.listFormatter(channelMentions, true), false);
            } else {
                infoEmbed.addField("Channels", "You do not have access to any channels that you are able to run this command in.", false);
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

    public boolean isAlias(CommandObject command, String subType) {
        return command.message.get().getContent().toLowerCase().startsWith(command.guild.config.getPrefixCommand() + subType.toLowerCase());
    }

    public boolean hasSubCommands(SAILType type) {
        boolean subFound = false;
        for (SubCommandObject sb : subCommands) {
            if (sb.getType() == type) {
                subFound = true;
                break;
            }
        }
        return subFound;
    }

    public boolean isVisibleInType(CommandObject commandObject, SAILType type) {
        if (this.type == type) {
            return GuildHandler.testForPerms(commandObject, perms);
        } else {
            boolean hasPerms = false;
            for (SubCommandObject s : subCommands) {
                List<Permission> allPerms = new ArrayList<>(Arrays.asList(perms));
                allPerms.addAll(Arrays.asList(s.getPermissions()));
                if (GuildHandler.testForPerms(commandObject, allPerms)) {
                    hasPerms = true;
                }
            }
            return hasPerms;
        }
    }

    public boolean isName(String args, CommandObject command) {
        String prefix = command.guild.config.getPrefixCommand();
        List<String> allNames = new ArrayList<>(Arrays.asList(names));
        for (SubCommandObject s : subCommands) {
            if (GuildHandler.testForPerms(command, s.getPermissions())) {
                allNames.addAll(Arrays.asList(s.getNames()));
            }
        }
        allNames = allNames.stream().distinct().collect(Collectors.toList());
        for (String s : allNames) {
            if (s.equalsIgnoreCase(args) || args.equalsIgnoreCase(prefix + s)) {
                return true;
            }
        }
        return false;
    }

    public boolean testSubCommands(CommandObject command, List<SAILType> types) {
        for (SubCommandObject s : subCommands) {
            if (types.contains(s.getType()) && GuildHandler.testForPerms(command, s.getPermissions())) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Command> T get(Class obj) {
        return CommandList.getCommand(obj);
    }


}
