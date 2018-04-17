package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings extends Command {

    private static String settings(CommandObject command) {
        String settings = "**Settings**";
        if (command.guild.config.modulePixels) {
            settings += "\n> " + UserSetting.DENIED_XP +
                    "\n> " + UserSetting.DONT_SHOW_LEADERBOARD +
                    "\n> " + UserSetting.DENY_AUTO_ROLE +
                    "\n> " + UserSetting.DONT_DECAY;
        }
        if (command.guild.config.moduleCC) {
            settings += "\n> " + UserSetting.DENY_MAKE_CC +
                    "\n> " + UserSetting.DENY_USE_CCS +
                    "\n> " + UserSetting.AUTO_SHITPOST;
        }
        if (command.guild.config.artPinning) {
            settings += "\n> " + UserSetting.DENY_ART_PINNING;
        }
        settings += "\n> " + UserSetting.DENY_INVITES;
        settings += "\n> List - `Shows the user's settings.`";
        return settings;
    }

    @Override
    public String execute(String args, CommandObject command) {
        if (args == null || args.isEmpty()) return settings(command) + "\n\n" + missingArgs(command);
        SplitFirstObject split = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, split.getFirstWord(), false);
        if (user == null) {
            return "> Could not find user.";
        }
        ProfileObject profile = user.getProfile(command.guild);
        if (profile != null) {
            if (split.getRest() == null || split.getRest().equalsIgnoreCase("list")) {
                return sendList("", profile, command, user, false);
            }
            UserSetting toTest = UserSetting.get(split.getRest());
            if (toTest == null) return "> Not a valid user setting.\n" + settings(command);
            if (command.guild.config.modulePixels) {
                switch (toTest) {
                    case DENIED_XP:
                        return profile.toggleSetting(UserSetting.DENIED_XP,
                                "> **" + user.displayName + "** will now gain xp again.",
                                "> **" + user.displayName + "** will no longer gain XP.");
                    case DONT_SHOW_LEADERBOARD:
                        return profile.toggleSetting(UserSetting.DONT_SHOW_LEADERBOARD,
                                "> **" + user.displayName + "'s** rank is now visible.",
                                "> **" + user.displayName + "** will no longer show their rank.");
                    case DONT_DECAY:
                        return profile.toggleSetting(UserSetting.DONT_DECAY,
                                "> **" + user.displayName + "** will now have pixel decay.",
                                "> **" + user.displayName + "** will no longer have pixel decay.");
                    case DENY_AUTO_ROLE:
                        return profile.toggleSetting(UserSetting.DENY_AUTO_ROLE,
                                "> **" + user.displayName + "** will now automatically be granted roles.",
                                "> **" + user.displayName + "** will no longer automatically be granted roles.");
                }
            }
            if (command.guild.config.moduleCC) {
                switch (toTest) {
                    case DENY_MAKE_CC:
                        return profile.toggleSetting(UserSetting.DENY_MAKE_CC,
                                "> **" + user.displayName + "** can now make custom commands.",
                                "> **" + user.displayName + "** can no longer make custom commands.");
                    case DENY_USE_CCS:
                        return profile.toggleSetting(UserSetting.DENY_USE_CCS,
                                "> **" + user.displayName + "** can now use custom commands.",
                                "> **" + user.displayName + "** can no longer use custom commands.");
                    case AUTO_SHITPOST:
                        return profile.toggleSetting(UserSetting.AUTO_SHITPOST,
                                "> **" + user.displayName + "** no longer has the shitpost tag automatically added to all of their new Custom Commands.",
                                "> **" + user.displayName + "** now has the shitpost tag automatically added to all of their new Custom Commands.");
                }
            }
            if (command.guild.config.artPinning) {
                switch (toTest) {
                    case DENY_ART_PINNING:
                        return profile.toggleSetting(UserSetting.DENY_ART_PINNING,
                                "> **" + user.displayName + "** can now pin art.",
                                "> **" + user.displayName + "** can no longer pin art.");
                }
            }
            switch (toTest) {
                case DENY_INVITES:
                    return profile.toggleSetting(UserSetting.DENY_INVITES,
                            "> **" + user.displayName + "** can now post instant invites.",
                            "> **" + user.displayName + "** can no longer post instant invites.");
                default:
                    String response = (split.getRest() == null || split.getRest().isEmpty()) ? "" : "> Not a valid User Setting.\n\n";
                    if (profile.getSettings().size() == 0) {
                        return response + "> **" + user.displayName + "** has no settings attached to their profile.\n\n" + settings(command) + "\n\n" + Utility.getCommandInfo(this, command);
                    } else {
                        return sendList(response, profile, command, user, true);
                    }
            }
        } else {
            return "> Invalid user.";
        }
    }

    private String sendList(String prefix, ProfileObject profile, CommandObject command, UserObject user, boolean showCommand) {
        List<String> userSettings = new ArrayList<>();
        for (UserSetting s : profile.getSettings()) {
            userSettings.add(s.toString());
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(user.displayName + "'s User settings:");
        if (userSettings.size() == 0) {
            return "> **" + user.displayName + "** has no settings on their profile.";
        } else {
            String desc = "```\n" + Utility.listFormatter(userSettings, true) + "```";
            if (showCommand) {
                desc += "\n" + Utility.getCommandInfo(this, command);
            }
            builder.withDesc(desc);
        }
        RequestHandler.sendEmbedMessage(prefix, builder, command.channel.get());
        return "";
    }

    @Override
    protected String[] names() {
        return new String[]{"UserSettings"};
    }

    @Override
    public String description(CommandObject command) {
        return "allows setting of certain user settings.\n" + settings(command);
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
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES, Permissions.MANAGE_ROLES};
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

    }
}
