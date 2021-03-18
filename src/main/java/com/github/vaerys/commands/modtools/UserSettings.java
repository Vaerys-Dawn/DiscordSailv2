package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings extends Command {

    private static String settings(CommandObject command) {
        StringHandler settings = new StringHandler("**Settings**");
        if (command.guild.config.modulePixels) {
            settings.append("\\> " + UserSetting.DENIED_XP)
                    .append("\\> " + UserSetting.DONT_SHOW_LEADERBOARD)
                    .append("\\> " + UserSetting.DENY_AUTO_ROLE)
                    .append("\\> " + UserSetting.DONT_DECAY);
        }
        if (command.guild.config.moduleCC) {
            settings.append("\\> " + UserSetting.DENY_MAKE_CC)
                    .append("\\> " + UserSetting.DENY_USE_CCS)
                    .append("\\> " + UserSetting.AUTO_SHITPOST);
        }
        if (command.guild.config.artPinning) {
            settings.append("\\> " + UserSetting.DENY_ART_PINNING);
        }
        if (command.guild.config.readRuleReward) {
            settings.append("\\> " + UserSetting.READ_RULES);
        }
        settings.append("\\> " + UserSetting.DENY_INVITES);
        settings.append("\\> List - `Shows the globalUser's settings.`");
        return settings.toString();
    }

    private static final String LINE_PREFIX = "\\> **";

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) return settings(command) + "\n\n" + missingArgs(command);
        SplitFirstObject split = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, split.getFirstWord(), false);
        if (user == null) {
            return "\\> Could not find globalUser.";
        }
        ProfileObject profile = user.getProfile();
        if (profile != null) {
            if (split.getRest() == null || split.getRest().equalsIgnoreCase("list")) {
                return sendList("", profile, command, user, false);
            }
            UserSetting toTest = UserSetting.get(split.getRest());
            if (toTest == null) return "\\> Not a valid globalUser setting.\n" + settings(command);
            if (command.guild.config.modulePixels) {
                switch (toTest) {
                    case DENIED_XP:
                        return profile.toggleSetting(UserSetting.DENIED_XP,
                                LINE_PREFIX + user.displayName + "** will now gain xp again.",
                                LINE_PREFIX + user.displayName + "** will no longer gain XP.");
                    case DONT_SHOW_LEADERBOARD:
                        return profile.toggleSetting(UserSetting.DONT_SHOW_LEADERBOARD,
                                LINE_PREFIX + user.displayName + "'s** rank is now visible.",
                                LINE_PREFIX + user.displayName + "** will no longer show their rank.");
                    case DONT_DECAY:
                        return profile.toggleSetting(UserSetting.DONT_DECAY,
                                LINE_PREFIX + user.displayName + "** will now have pixel decay.",
                                LINE_PREFIX + user.displayName + "** will no longer have pixel decay.");
                    case DENY_AUTO_ROLE:
                        return profile.toggleSetting(UserSetting.DENY_AUTO_ROLE,
                                LINE_PREFIX + user.displayName + "** will now automatically be granted roles.",
                                LINE_PREFIX + user.displayName + "** will no longer automatically be granted roles.");
                    default:
                        // do nothing
                }
            }
            if (command.guild.config.moduleCC) {
                switch (toTest) {
                    case DENY_MAKE_CC:
                        return profile.toggleSetting(UserSetting.DENY_MAKE_CC,
                                LINE_PREFIX + user.displayName + "** can now make custom commands.",
                                LINE_PREFIX + user.displayName + "** can no longer make custom commands.");
                    case DENY_USE_CCS:
                        return profile.toggleSetting(UserSetting.DENY_USE_CCS,
                                LINE_PREFIX + user.displayName + "** can now use custom commands.",
                                LINE_PREFIX + user.displayName + "** can no longer use custom commands.");
                    case AUTO_SHITPOST:
                        return profile.toggleSetting(UserSetting.AUTO_SHITPOST,
                                LINE_PREFIX + user.displayName + "** no longer has the shitpost tag automatically added to all of their new Custom Commands.",
                                LINE_PREFIX + user.displayName + "** now has the shitpost tag automatically added to all of their new Custom Commands.");
                    default:
                        // do nothing
                }
            }
            if (command.guild.config.artPinning && toTest == UserSetting.DENY_ART_PINNING) {
                return profile.toggleSetting(UserSetting.DENY_ART_PINNING,
                        LINE_PREFIX + user.displayName + "** can now pin art.",
                        LINE_PREFIX + user.displayName + "** can no longer pin art.");
            }
            if (command.guild.config.readRuleReward && toTest == UserSetting.READ_RULES) {
                String response = profile.toggleSetting(UserSetting.READ_RULES,
                        "\\> Read Rules status has been disabled for **" + user.displayName + "**.",
                        "\\> Read Rules status has been enabled for **" + user.displayName + "**.");
                if (profile.hasSetting(UserSetting.READ_RULES)) {
                    command.guild.get().addRoleToMember(profile.getUser(command.guild).getMember(), command.guild.getRuleCodeRole()).queue();
                } else {
                    command.guild.get().removeRoleFromMember(profile.getUser(command.guild).getMember(), command.guild.getRuleCodeRole()).queue();
                }
                return response;
            }
            if (toTest == UserSetting.DENY_INVITES) {
                return profile.toggleSetting(UserSetting.DENY_INVITES,
                        LINE_PREFIX + user.displayName + "** can now post instant invites.",
                        LINE_PREFIX + user.displayName + "** can no longer post instant invites.");
            }
            String response = (split.getRest() == null || split.getRest().isEmpty()) ? "" : "> Not a valid User Setting.\n\n";
            if (profile.getSettings().size() == 0) {
                return response + LINE_PREFIX + user.displayName + "** has no settings attached to their profile.\n\n" + settings(command) + "\n\n" + missingArgs(command);
            } else {
                return sendList(response, profile, command, user, true);
            }
        } else {
            return "\\> Invalid globalUser.";
        }
    }

    private String sendList(String prefix, ProfileObject profile, CommandObject command, UserObject user, boolean showCommand) {
        List<String> userSettings = new ArrayList<>();
        for (UserSetting s : profile.getSettings()) {
            userSettings.add(s.toString());
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(user.displayName + "'s User settings:");
        if (userSettings.isEmpty()) {
            return LINE_PREFIX + user.displayName + "** has no settings on their profile.";
        } else {
            String desc = "```\n" + Utility.listFormatter(userSettings, true) + "```";
            if (showCommand) {
                desc += "\n" + missingArgs(command);
            }
            builder.setDescription(desc);
        }
        command.guildChannel.queueMessage(prefix, builder.build());
        return "";
    }

    @Override
    protected String[] names() {
        return new String[]{"UserSettings", "UserSetting", "USetting"};
    }

    @Override
    public String description(CommandObject command) {
        return "allows setting of certain globalUser settings.\n" + settings(command);
    }

    @Override
    protected String usage() {
        return "[@User] [Setting]";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permission.MESSAGE_MANAGE, Permission.MANAGE_ROLES};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {
        // does nothing
    }
}
