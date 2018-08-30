package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class ListTags extends Command {

    private static final SubCommandObject TAGS = new SubCommandObject(new String[]{"Tags"}, "",
            "Shows the cc tags.", SAILType.CC);
    private static final SubCommandObject TAGS_ADMIN = new SubCommandObject(new String[]{"AdminTags", "TagsAdmin"}, "",
            "Shows the cc tags and admin cc tags in one list.", SAILType.ADMIN_CC, Permissions.MANAGE_SERVER, Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES);


    public static List<String> getModes(CommandObject command) {
        List<String> modes = new ArrayList<String>() {{
            add(TagType.INFO.toString());
            add(TagType.DAILY.toString());
            add(TagType.LEVEL.toString());
        }};
        if (command.guild.config.moduleCC) {
            modes.add(TagType.CC.toString());
        }
        if (command.guild.config.moduleAdminCC) {
            modes.add(TagType.ADMIN_CC.toString());
        }
        return modes;
    }


    @Override
    public String execute(String args, CommandObject command) {
        List<String> list = new LinkedList<>();
        XEmbedBuilder builder = new XEmbedBuilder(command);
        TagType type = TagType.get(args);
        boolean isTags = TAGS.isSubCommand(command);
        boolean isAdminTags = TAGS_ADMIN.isSubCommand(command);

        if (!isTags && !isAdminTags) {
            if (args.isEmpty()) {
                sendModes(command, "");
                return null;
            } else {
                list = TagList.getType(type).stream().map(t -> t.name).collect(Collectors.toList());
            }
        } else if (TAGS_ADMIN.isSubCommand(command)) {
            List<TagObject> tags = TagList.getType(TagType.CC);
            tags.addAll(TagList.getType(TagType.ADMIN_CC));
            TagList.sort(tags);
            list = tags.stream().map(t -> t.name).collect(Collectors.toList());
        } else if (TAGS.isSubCommand(command)) {
            list = TagList.getNames(TagType.CC);
        }
        if (list.isEmpty()) {
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
        builder.withDesc("```\n" + Utility.listFormatter(getModes(command), false) + "```\n" + missingArgs(command));
        RequestHandler.sendEmbedMessage(s, builder, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"ListTags", "TagList"};
    }

    @Override
    public String description(CommandObject command) {
        String formatted = Utility.listFormatter(getModes(command).stream().map(s -> "> " + s).collect(Collectors.toList()), false);
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
        subCommands.add(TAGS_ADMIN);
        showIndividualSubs = true;
    }
}
