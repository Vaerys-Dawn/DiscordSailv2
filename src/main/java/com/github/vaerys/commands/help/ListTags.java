package com.github.vaerys.commands.help;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import sx.blah.discord.handle.obj.Permissions;


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

    protected static final String[] NAMES = new String[]{"Tags", "ListTags"};
    @Override
    protected String[] names() {
        return NAMES;
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

    protected static final String USAGE = "(TagType)";
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
