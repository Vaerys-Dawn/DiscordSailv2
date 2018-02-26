package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
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
            list = TagList.getNames(TagType.CC);
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        if (args.isEmpty()) args = TagType.CC.toString();
        builder.withTitle("> Here are all of the " + StringUtils.capitalize(args) + " tags:");
        builder.withDesc("```\n" + Utility.listFormatter(list, true) + "```\n" +
                "Tags are run in the order listed above.\n\n" + new HelpTags().missingArgs(command));
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
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
    protected String usage() {
        return "(TagType)";
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
