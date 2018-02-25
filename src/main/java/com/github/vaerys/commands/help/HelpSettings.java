package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class HelpSettings extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new HelpModules().getInfo(false, args, command);
        if (builder == null) {
            return "> Could not find any settings named **" + args + "**.";
        } else {
            RequestHandler.sendEmbedMessage("", builder, command.channel.get());
            return null;
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"HelpSetting", "HelpSettings", "SettingHelp", "HelpToggle", "ToggleHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a setting.";
    }

    @Override
    protected String usage() {
        return "[Setting Name]";
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
        return  new Permissions[]{Permissions.MANAGE_SERVER};
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