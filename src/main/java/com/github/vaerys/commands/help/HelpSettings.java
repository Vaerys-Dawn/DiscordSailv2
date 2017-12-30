package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class HelpSettings implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new HelpModules().getInfo(false, args,command);
        if (builder == null) {
            return "> Could not find any settings named **" + args + "**.";
        } else {
            RequestHandler.sendEmbedMessage("", builder, command.channel.get());
            return null;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"HelpSetting","HelpSettings","SettingHelp","HelpToggle","ToggleHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a setting.";
    }

    @Override
    public String usage() {
        return "[Setting Name]";
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
        return new Permissions[]{Permissions.MANAGE_SERVER};
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