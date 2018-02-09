package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.ChannelHere;
import com.github.vaerys.commands.admin.Module;
import com.github.vaerys.commands.admin.Toggle;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
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

    @Override
    public String[] names() {
        return new String[]{"StartUpGuide"};
    }

    @Override
    public String description(CommandObject command) {
        return "Posts a link to the Bot's Startup Guide on its wiki.";
    }

    @Override
    public String usage() {
        return null;
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
    public void init() {

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
