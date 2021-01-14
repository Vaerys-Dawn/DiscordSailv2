package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class HelpSettings extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new HelpModules().getInfo(false, args, command);
        if (builder == null) {
            return "\\> Could not find any settings named **" + args + "**.";
        } else {
            builder.queue(command);
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
