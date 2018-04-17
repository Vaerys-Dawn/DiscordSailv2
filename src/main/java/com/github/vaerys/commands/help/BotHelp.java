package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
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
        builder.withDesc("> `[]` and `()` in the command usage are not needed and in most cases can cause the command to fail.\n" +
                "> `[]` brackets means that this argument is required.\n" +
                "> `()` brackets means that this argument is optional.\n" +
                "> When an argument ends in `...` it means you can input as many of this argument as you like.\n" +
                "> The `@User` argument means that you can either use a user ID, mention or the user's display name or username as an argument.\n" +
                "> The `Time` argument means that you can enter a number or a number ending in `d` for days, `h` for hours, `m` for minutes or `s` for seconds.");
        builder.send(command.channel);
        return null;
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
