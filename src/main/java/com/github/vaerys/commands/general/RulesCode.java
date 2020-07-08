package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.TextChannel;
import sx.blah.discord.handle.obj.Role;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.regex.Pattern;

public class RulesCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.message.delete();
        List<TextChannel> botCommands = command.user.getVisibleChannels(command.guild.getChannelsByType(ChannelSetting.BOT_COMMANDS));
        if (!botCommands.isEmpty() && !botCommands.contains(command.channel.get())
                && !GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
            return Utility.getChannelMessage(botCommands);
        }
        if (command.guild.config.getRuleCode() == null) {
            return "\\> no rule code exists try again later.";
        }
        ProfileObject profile = command.user.getProfile(command.guild);
        if (profile == null)
            return "\\> **" + command.user.displayName + "** An error occurred, You do not have a profile yet. please dm me this error as this should never happen.";
        if (profile.getSettings().contains(UserSetting.READ_RULES)) {
            GuildHandler.checkUsersRoles(command.user.longID, command.guild);
            return "\\> **" + command.user.displayName + "** You have already guessed the code correctly.";
        }
        if (args.equalsIgnoreCase(command.guild.config.getRuleCode())) {
            profile.getSettings().add(UserSetting.READ_RULES);
            String response = "\\> Congratulations you have guessed the Rule Code correctly, A Star";
            if (command.guild.config.xpGain) {
                response += " and " + (int) (200 * command.guild.config.xpModifier) + " Pixels have been added to your profile.";
                profile.addXP(200, command.guild.config);
            } else {
                response += " has been added to your profile.";
            }
            Role ruleReward = command.guild.getRuleCodeRole();
            if (ruleReward != null) {
                response += "\nYou have also been granted the **" + ruleReward.getName() + "** Role.";
            }
            response += "\n\n**Reminder:** Do not share the secret code, it wouldn't be much of a secret if you did.";
            GuildHandler.checkUsersRoles(command.user.longID, command.guild);
            command.user.sendDm(response);
            return null;
        }
        int diff = (command.guild.config.getRuleCode().length() / 4);
        if (diff < 2) diff += 2;
        if (!Pattern.compile("\\[(.|\n)*]").matcher(command.guild.config.getRuleCode()).matches() && Pattern.compile("\\[(.|\n)*]").matcher(args).matches()) {
            command.user.sendDm("\\> That was not the right code, please try again.\n" +
                    "The brackets are not part of the code.");
        } else if ((Math.abs(command.guild.config.getRuleCode().length() - args.length()) <= diff) &&
                (StringUtils.containsIgnoreCase(command.guild.config.getRuleCode(), args) ||
                        (StringUtils.containsIgnoreCase(args, command.guild.config.getRuleCode())))) {
            command.user.sendDm("\\> That was not the code, but you were getting close.");
            return null;
        } else {
            command.user.sendDm("\\> That was not the right code, please try again.\n");
        }
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"RuleCode", "RulesCode"};
    }

    @Override
    public String description(CommandObject command) {
        if (command.guild.config.xpGain) {
            return "Enter the rule code found in the rules to receive rewards.";
        } else {
            return "Enter the rule code found in the rules to get a star on your profile.";
        }
    }

    @Override
    protected String usage() {
        return "[Secret code]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
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
