package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class HelpTags implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (TagObject t : TagList.get()) {
            if (t.name.equalsIgnoreCase(args) || t.name.equalsIgnoreCase("<" + args + ">")) {
                Utility.sendEmbedMessage("", t.getInfo(command), command.channel.get());
                return null;
            }
        }
        return "> Could not find tag.";
    }

    @Override
    public String[] names() {
        return new String[]{"HelpTag", "HelpTags","TagHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a specific tag";
    }

    @Override
    public String usage() {
        return "[TagName]";
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
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
