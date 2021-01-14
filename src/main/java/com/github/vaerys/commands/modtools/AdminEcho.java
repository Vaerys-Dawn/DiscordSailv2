package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagRemoveMentions;
import com.github.vaerys.tags.cctags.TagRemovePrep;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.ListIterator;

public class AdminEcho extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<TagObject> tags = TagList.getType(TagType.CC);
        ListIterator<TagObject> iterator = tags.listIterator();
        while (iterator.hasNext()) {
            TagObject object = iterator.next();
            if (object.name.equals(new TagRemoveMentions(0).name)) {
                iterator.remove();
            }
        }
        args += TagList.getTag(TagRemovePrep.class).name;
        TagList.sort(tags);
        for (TagObject t : tags) {
            args = t.handleTag(args, command, "");
        }
        return args;
    }


    public void HelloWorld(){}

    @Override
    public String description(CommandObject command) {
        return "acts like " + command.guild.config.getPrefixCC() + "echo but doesn't have any mention sanitisation.";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"AdminEcho"};
    }

    @Override
    protected String usage() {
        return "[Text]";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_MENTION_EVERYONE};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

}
