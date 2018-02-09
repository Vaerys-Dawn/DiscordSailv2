package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Pattern;

public class RulesCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.getRuleCode() == null) {
            return "> no rule code exists try again later.";
        }
        command.message.delete();
        ProfileObject profile = command.user.getProfile(command.guild);
        if (profile == null)
            return "> **" + command.user.displayName + "** An error occurred, You do not have a profile yet. please dm me this error as this should never happen.";
        if (profile.getSettings().contains(UserSetting.READ_RULES))
            return "> **" + command.user.displayName + "** You have already guessed the code correctly.";
        if (args.equalsIgnoreCase(command.guild.config.getRuleCode())) {
            profile.getSettings().add(UserSetting.READ_RULES);
            if (command.guild.config.xpGain) {
                command.user.sendDm("> Congratulations you have been granted some pixels and a star has been added to your profile for reading the rules.\n" +
                        "Never tell this code to anyone.");
                profile.addXP(200, command.guild.config);
                return null;
            } else {
                command.user.sendDm("> Congratulations a star has been added to your profile for reading the rules.\n" +
                        "Never tell this code to anyone.");
                return null;
            }
        }
        int diff = (command.guild.config.getRuleCode().length() / 4);
        if (diff < 2) diff += 2;
        if (!Pattern.compile("\\[(.|\n)*]").matcher(command.guild.config.getRuleCode()).matches() && Pattern.compile("\\[(.|\n)*]").matcher(args).matches()) {
            command.user.sendDm("> That was not the right code, please try again.\n" +
                    "The brackets are not part of the code.");
        } else if ((Math.abs(command.guild.config.getRuleCode().length() - args.length()) <= diff) &&
                (StringUtils.containsIgnoreCase(command.guild.config.getRuleCode(), args) ||
                        (StringUtils.containsIgnoreCase(args, command.guild.config.getRuleCode())))) {
            command.user.sendDm("> That was not the code, but you were getting close.");
            return null;
        } else {
            command.user.sendDm("> That was not the right code, please try again.\n");
        }
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"RulesCode", "RuleCode"};
    }

    @Override
    public String description(CommandObject command) {
        if (command.guild.config.xpGain) {
            return "Enter the rule code found in the rules to receive a rewards.";
        } else {
            return "Enter the rule code found in the rules to get a star on your profile.";
        }
    }

    @Override
    public String usage() {
        return "[Secret code]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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