package com.github.vaerys.commands.help;

import com.github.vaerys.commands.admin.ChannelHere;
import com.github.vaerys.commands.admin.Module;
import com.github.vaerys.commands.admin.Toggle;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 22/02/2017.
 */
public class StartUpGuide extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setAuthor("Start Up Guide.");
        builder.setTitle("Helpful Commands.");
        String desc = "**" + new Commands().getUsage(command) + "**\n" +
                "Lists all commands.\n**" +
                get(Help.class).getUsage(command) + "**\n" +
                "Gives you information about a command.\n**" +
                get(Module.class).getUsage(command) + "**\n" +
                "Lists all available modules and allows you to toggle them.\n**" +
                get(HelpModules.class).getUsage(command) + "**\n" +
                "Gives you information about a module.\n**" +
                get(Toggle.class).getUsage(command) + "**\n" +
                "Lists all available settings and allows you to toggle them.\n**" +
                get(HelpSettings.class).getUsage(command) + "**\n" +
                "Gives you information about a setting.\n**" +
                get(ChannelHere.class).getUsage(command) + "**\n" +
                "Lists all available channel settings/types and allows you to toggle them.\n**" +
                get(HelpChannel.class).getUsage(command) + "**\n" +
                "Gives you information about a channel setting/type.\n**" +
                get(ListTags.class).getUsage(command) + "**\n" +
                "Lists all tags available for use in customCommand, Info, Daily and LevelUp messages.\n**" +
                get(HelpTags.class).getUsage(command) + "**\n" +
                "Gives you information about a tag.\n**" +
                get(GetGuildInfo.class).getUsage(command) + "**\n" +
                "Gives you information about this server's setup.\n**" +
                get(BotInfo.class).getUsage(command) + "**\n" +
                "Gives you information about this bot.\n**" +
                get(BotHelp.class).getUsage(command) + "**\n" +
                "Gives you information about various bot features.";
        builder.setDescription(desc);
        builder.queue(command);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"StartUpGuide"};
    }

    @Override
    public String description(CommandObject command) {
        return "Posts a link to the Bot's Startup Guide on its wiki.";
    }

    @Override
    protected String usage() {
        return null;
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
