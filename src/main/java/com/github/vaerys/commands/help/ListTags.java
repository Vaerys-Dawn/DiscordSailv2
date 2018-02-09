package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;


public class ListTags extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<String> list = TagList.getNames(args);
        if (list.size() == 0) {
            list = TagList.getNames(TagList.CC);
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        if (args.isEmpty()) args = TagList.CC;
        builder.withTitle("> Here are all of the " + StringUtils.capitalize(args) + " tags:");
        builder.withDesc("```\n" + Utility.listFormatter(list, true) + "```\n" +
                "Tags are run in the order listed above.\n\n" + new HelpTags().missingArgs(command));
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Tags", "ListTags"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists all of a certain type of tag, defaults to Custom command tags.\n" +
                "**Tag Types:**\n" +
                "> CustomCommand\n" +
                "> Info\n" +
                "> Daily\n" +
                "> LevelUp\n";
    }

    @Override
    public String usage() {
        return "(TagType)";
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
