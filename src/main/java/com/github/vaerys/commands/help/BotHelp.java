package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Client;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class BotHelp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String botName = command.botUser.displayName;
        String missingArgs = missingArgs(command);
        XEmbedBuilder builder = new XEmbedBuilder();
        switch (args.toLowerCase()) {
            case "arguments":
                return arguments(builder, command.guildChannel);
            case "dms":
                return directMessages(builder, command.guildChannel, botName);
            case "tags":
                return tags(builder, command.guildChannel);
            default:
                return defaultOutput(builder, command.guildChannel, botName, missingArgs);
        }
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        String botName = Client.getClientObject().bot.name;
        String missingArgs = missingArgsDm();
        XEmbedBuilder builder = new XEmbedBuilder();
        switch (args.toLowerCase()) {
            case "arguments":
                return arguments(builder, command.messageChannel);
            case "dms":
                return directMessages(builder, command.messageChannel, botName);
            case "tags":
                return tags(builder, command.messageChannel);
            default:
                return defaultOutput(builder, command.messageChannel, botName, missingArgs);
        }
    }

    private String defaultOutput(XEmbedBuilder builder, ChannelObject channel, String botName, String missingArgs) {
        builder.setTitle("Bot Help.");
        String desc = "**Modes:**\n" +
                "\\> **Arguments**\n" +
                "Gives you information about how to use command arguments.\n" +
                "\\> **DMs**\n" +
                "Gives you information about how Direct messages work for **" + botName + "**.\n" +
                "\\> **Tags**\n" +
                "Gives you information explaining what tags are.\n\n" + missingArgs;
        builder.setDescription(desc);
        builder.queue(channel);
        return null;
    }

    private String tags(XEmbedBuilder builder, ChannelObject channel) {
        builder.setTitle("Tags.");
        builder.setDescription("\\> Tags are strings of text that when added to CustomCommands, The info page generator, Daily Messages or Level up messages run specific code and change the output text.");
        builder.queue(channel);
        return null;
    }

    private String directMessages(XEmbedBuilder builder, ChannelObject channel, String botName) {
        builder.setTitle("Direct messages.");
        builder.setDescription("\\> When you send a non command Direct message to " + botName + " it will send it to the bot developer.");
        builder.queue(channel);
        return null;
    }

    private String arguments(XEmbedBuilder builder, ChannelObject channel) {
        builder.setTitle("Arguments.");
        builder.setDescription("\\> `[]` and `()` in the command usage are not needed and in most cases can cause the command to fail.\n" +
                "\\> `[]` brackets means that this argument is required.\n" +
                "\\> `()` brackets means that this argument is optional.\n" +
                "\\> When an argument ends in `...` it means you can input as many of this argument as you like.\n" +
                "\\> The `@User` argument means that you can either use a user ID, mention or the user's display name or username as an argument.\n" +
                "\\> The `Time` argument means that you can enter a number or a number ending in `d` for days, `h` for hours, `m` for minutes or `s` for seconds.");
        builder.queue(channel);
        return null;
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"BotHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you some information about some of the bot's features.";
    }

    @Override
    protected String usage() {
        return "(Mode)";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
