package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
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
        return "\\> Could not find tag.";
    }

    @Override
    protected String[] names() {
        return new String[]{"HelpTag", "HelpTags", "TagHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives you information about a specific tag";
    }

    @Override
    protected String usage() {
        return "[TagName]";
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
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
