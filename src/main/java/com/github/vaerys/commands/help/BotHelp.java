package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

public class BotHelp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        switch (args.toLowerCase()) {
            case "arguments":
                return arguments(command);
            case "dms":
                return directMessages(command);
            case "tags":
                return tags(command);
            default:
                return defaultOutput(command);
        }
    }

    private String defaultOutput(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Bot Help.");
        builder.withDesc("**Modes:**\n" +
                "> **Arguments**\n" +
                "Gives you information about how to use command arguments.\n" +
                "> **DMs**\n" +
                "Gives you information about how Direct messages work for **" + command.client.bot.displayName + "**.\n" +
                "> **Tags**\n" +
                "Gives you information explaining what tags are.\n\n" + missingArgs(command));
        builder.send(command.channel);
        return null;
    }

    private String tags(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Tags.");
        builder.withDesc("> Tags are strings of text that when added to CustomCommands, The info page generator, Daily Messages or Level up messages run specific code and change the output text.");
        builder.send(command.channel);
        return null;
    }

    private String directMessages(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Direct messages.");
        builder.withDesc("> When you send a non command Direct message to " + command.client.bot.displayName + " it will send it to the bot developer.");
        builder.send(command.channel);
        return null;
    }

    private String arguments(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Arguments.");
        builder.withDesc("> `[]` and `()` in the command usage are not needed and in some cases can cause the command to fail.\n" +
                "> `[]` brackets means that this argument is required.\n" +
                "> `()` brackets means that this argument is optional.\n" +
                "> When an argument ends in `...` it means you can input as many of this argument as you like.\n" +
                "> The `@User` argument means that you can either use a user ID, mention or the user's display name or username as an argument.\n" +
                "> The `Time` argument means that you can enter a number or a number ending in `d` for days, `h` for hours, `m` for minutes or `s` for seconds.");
        builder.send(command.channel);
        return null;
    }

    protected static final String[] NAMES = new String[]{"BotHelp"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you some information about some of the bot's features.";
    }

    protected static final String USAGE = "(Mode)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.HELP;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
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