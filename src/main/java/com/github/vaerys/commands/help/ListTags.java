package com.github.vaerys.commands.help;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ListTags extends Command {

    private static final SubCommandObject TAGS = new SubCommandObject(new String[]{"Tags"}, "",
            "Shows the cc tags.", SAILType.CC);

    private static final List<String> modes = new ArrayList<String>() {{
        add(TagType.CC.toString());
        add(TagType.INFO.toString());
        add(TagType.DAILY.toString());
        add(TagType.LEVEL.toString());
    }};

    @Override
    public String execute(String args, CommandObject command) {
        List<String> list;
        XEmbedBuilder builder = new XEmbedBuilder(command);
        TagType type = TagType.get(args);
        if (args.isEmpty() && !TAGS.isSubCommand(command)) {
            sendModes(command, "");
            return null;
        } else if (TAGS.isSubCommand(command)) {
            list = TagList.getNames(TagType.CC);
        } else {
            list = TagList.getNames(type);
        }
        if (list.size() == 0) {
            sendModes(command, "> Invalid mode.");
            return null;
        }
        builder.withTitle("> Here are all of the " + StringUtils.capitalize(args) + " tags:");
        builder.withDesc("```\n" + Utility.listFormatter(list, true) + "```\n" +
                "Tags are run in the order listed above.\n\n" + new HelpTags().missingArgs(command));
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    private void sendModes(CommandObject command, String s) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("> Modes");
        builder.withDesc("```\n" + Utility.listFormatter(modes, false) + "```\n" + missingArgs(command));
        RequestHandler.sendEmbedMessage(s, builder, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"ListTags"};
    }

    @Override
    public String description(CommandObject command) {
        String formatted = Utility.listFormatter(modes.stream().map(s -> "> " + s).collect(Collectors.toList()), false);
        return "Lists all of a certain type of tag, defaults to Custom command tags.\n" +
                "**Tag Types:**\n" + formatted;
    }

    @Override
    protected String usage() {
        return "[TagType]";
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
        subCommands.add(TAGS);
        showIndividualSubs = true;
    }
}
