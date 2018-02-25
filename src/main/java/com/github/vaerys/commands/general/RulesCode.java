package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
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
            String response = "> Congratulations you have guessed the Rule Code correctly, A Star";
            if (command.guild.config.xpGain) {
                response += (200 * command.guild.config.xpModifier) + "and " + (200 * command.guild.config.xpModifier) + " Pixels have been added to your profile.";
                profile.addXP(200, command.guild.config);
            }
            GuildHandler.checkUsersRoles(command.guild.longID,command.guild);
//            if (command.guild.config.xpGain) {
//                command.user.sendDm("> Congratulations you have been granted some pixels and a star has been added to your profile for reading the rules.\n" +
//                        "Never tell this code to anyone.");

//            } else {
//                command.user.sendDm("> Congratulations a star has been added to your profile for reading the rules.\n" +
//                        "Never tell this code to anyone.");
//            }

            return null;
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

    protected static final String[] NAMES = new String[]{"RulesCode", "RuleCode"};

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        if (command.guild.config.xpGain) {
            return "Enter the rule code found in the rules to receive a rewards.";
        } else {
            return "Enter the rule code found in the rules to getToggles a star on your profile.";
        }
    }

    protected static final String USAGE = "[Secret code]";

    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.BOT_COMMANDS;

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}