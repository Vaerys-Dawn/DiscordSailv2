package com.github.vaerys.commands.mention;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.MentionCommand;
import net.dv8tion.jda.api.Permission;

public class SetPrefixAdminCC extends MentionCommand {

    @Override
    public String execute(String args, CommandObject command) {
        boolean isAlphanumeric = args.matches("[A-Za-z0-9]+");
        if (args.contains(" ") || args.contains("\n") || args.length() > 5 || args.contains("#") || args.contains("@") || isAlphanumeric) {
            return "\\> Not a valid Prefix.";
        }
        command.guild.config.setPrefixAdminCC(args);
        return "\\> Admin Custom Command Prefix set to: " + args;
    }

    @Override
    protected String[] names() {
        return new String[]{"SetPrefixAdminCC","SetAdminCCPrefix"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the prefix for Admin Custom Commands.";
    }

    @Override
    protected String usage() {
        return "[Prefix]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN_CC;
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
        return true;
    }
}
