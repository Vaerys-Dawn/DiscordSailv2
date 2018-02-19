package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class HelpTags extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (TagObject t : TagList.get()) {
            if (t.name.equalsIgnoreCase(args) || t.name.equalsIgnoreCase("<" + args + ">")) {
                RequestHandler.sendEmbedMessage("", t.getInfo(command), command.channel.get());
                return null;
            }
        }
        return "> Could not find tag.";
    }

    protected static final String[] NAMES = new String[]{"HelpTag", "HelpTags","TagHelp"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a specific tag";
    }

    protected static final String USAGE = "[TagName]";
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

    protected static final boolean REQUIRES_ARGS = true;
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
