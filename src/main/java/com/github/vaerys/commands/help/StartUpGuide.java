package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.ChannelHere;
import com.github.vaerys.commands.admin.Module;
import com.github.vaerys.commands.admin.Toggle;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 22/02/2017.
 */
public class StartUpGuide extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withAuthorName("Start Up Guide.");
        builder.withTitle("Helpful Commands.");
        String desc = "**" + new Help().getUsage(command) + "**\n" +
                "Lists all commands.\n**" +
                new Info().getUsage(command) + "**\n" +
                "Gives you information about a command.\n**" +
                new Module().getUsage(command) + "**\n" +
                "Lists all available modules and allows you to toggle them.\n**" +
                new HelpModules().getUsage(command) + "**\n" +
                "Gives you information about a module.\n**" +
                new Toggle().getUsage(command) + "**\n" +
                "Lists all available settings and allows you to toggle them.\n**" +
                new HelpSettings().getUsage(command) + "**\n" +
                "Gives you information about a setting.\n**" +
                new ChannelHere().getUsage(command) + "**\n" +
                "Lists all available channel settings/types and allows you to toggle them.\n**" +
                new HelpChannel().getUsage(command) + "**\n" +
                "Gives you information about a channel setting/type.\n**" +
                new ListTags().getUsage(command) + "**\n" +
                "Lists all tags available for use in customCommand, Info, Daily and LevelUp messages.\n**" +
                new HelpTags().getUsage(command) + "**\n" +
                "Gives you information about a tag.\n**" +
                new GetGuildInfo().getUsage(command) + "**\n" +
                "Gives you information about this server's setup.\n**" +
                new BotInfo().getUsage(command) + "**\n" +
                "Gives you information about this bot.\n**" +
                new BotHelp().getUsage(command) + "**\n" +
                "Gives you information about various bot features.";
        builder.withDesc(desc);
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    protected static final String[] NAMES = new String[]{"StartUpGuide"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Posts a link to the Bot's Startup Guide on its wiki.";
    }

    protected static final String USAGE = null;
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
