package com.github.vaerys.commands.admin;

import java.util.List;
import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagRemoveMentions;
import com.github.vaerys.tags.cctags.TagRemoveSanitizeTag;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.enums.TagType;
import sx.blah.discord.handle.obj.Permissions;

public class AdminEcho extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[] {"AdminEcho"};
    protected static final String USAGE = "[Text]";
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[] {Permissions.MANAGE_MESSAGES, Permissions.MENTION_EVERYONE};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;


    @Override
    public String execute(String args, CommandObject command) {
        List<TagObject> tags = TagList.getType(TagType.CC);
        ListIterator<TagObject> iterator = tags.listIterator();
        while (iterator.hasNext()) {
            TagObject object = (TagObject) iterator.next();
            if (object.name.equals(new TagRemoveMentions(0).name)) {
                iterator.remove();
            }
        }
        args += new TagRemoveSanitizeTag(0).name;
        TagList.sort(tags);
        for (TagObject t : tags) {
            args = t.handleTag(args, command, "");
        }
        return args;
    }

    @Override
    public String description(CommandObject command) {
        return "acts like " + command.guild.config.getPrefixCC() + "echo but doesn't have any mention sanitisation.";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

}
