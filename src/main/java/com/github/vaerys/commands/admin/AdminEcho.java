package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.cctags.TagRemoveMentions;
import com.github.vaerys.tags.cctags.TagRemoveSanitizeTag;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.ListIterator;

public class AdminEcho extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<TagObject> tags = TagList.getType(TagList.CC);
        ListIterator iterator = tags.listIterator();
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
    public String[] names() {
        return new String[]{"AdminEcho"};
    }

    @Override
    public String description(CommandObject command) {
        return "acts like " + command.guild.config.getPrefixCC() + "echo but doesn't have any mention sanitisation.";
    }

    @Override
    public String usage() {
        return "[Text]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES, Permissions.MENTION_EVERYONE};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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