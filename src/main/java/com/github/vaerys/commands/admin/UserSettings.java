package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings extends Command {


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
            if (split.getRest().equalsIgnoreCase("list")) {
                return sendList("", profile, command, user, false);
            }
            UserSetting toTest = UserSetting.get(split.getRest());
            if (toTest == null) return "> Not a valid user setting.\n" + settings(command);
            if (command.guild.config.modulePixels) {
                switch (toTest) {
                    case DENIED_XP:
                        return toggleSetting(profile, UserSetting.DENIED_XP,
                                "> **" + user.displayName + "** will now gain xp again.",
                                "> **" + user.displayName + "** will no longer gain XP.");
                    case DONT_SHOW_LEADERBOARD:
                        return toggleSetting(profile, UserSetting.DONT_SHOW_LEADERBOARD,
                                "> **" + user.displayName + "'s** rank is now visible.",
                                "> **" + user.displayName + "** will no longer show their rank.");
                    case DONT_DECAY:
                        return toggleSetting(profile, UserSetting.DONT_DECAY,
                                "> **" + user.displayName + "** will now have pixel decay.",
                                "> **" + user.displayName + "** will no longer have pixel decay.");
                    case DENY_AUTO_ROLE:
                        return toggleSetting(profile, UserSetting.DENY_AUTO_ROLE,
                                "> **" + user.displayName + "** will now automatically be granted roles.",
                                "> **" + user.displayName + "** will no longer automatically be granted roles.");
                }
            }
            if (command.guild.config.moduleCC) {
                switch (toTest) {
                    case DENY_MAKE_CC:
                        return toggleSetting(profile, UserSetting.DENY_MAKE_CC,
                                "> **" + user.displayName + "** can now make custom commands.",
                                "> **" + user.displayName + "** can no longer make custom commands.");
                    case DENY_USE_CCS:
                        return toggleSetting(profile, UserSetting.DENY_USE_CCS,
                                "> **" + user.displayName + "** can now use custom commands.",
                                "> **" + user.displayName + "** can no longer use custom commands.");
                    case AUTO_SHITPOST:
                        return toggleSetting(profile, UserSetting.AUTO_SHITPOST,
                                "> **" + user.displayName + "** no longer has the shitpost tag automatically added to all of their new Custom Commands.",
                                "> **" + user.displayName + "** now has the shitpost tag automatically added to all of their new Custom Commands.");
                }
            }
            if (command.guild.config.artPinning) {
                switch (toTest) {
                    case DENY_ART_PINNING:
                        return toggleSetting(profile, UserSetting.DENY_ART_PINNING,
                                "> **" + user.displayName + "** can now pin art.",
                                "> **" + user.displayName + "** can no longer pin art.");
                }
            }
            switch (toTest) {
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
        settings += "\n> List - `Shows the user's settings.`";
        return settings;
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

    private String toggleSetting(ProfileObject user, UserSetting setting, String remove, String add) {
        if (user.getSettings().contains(setting)) {
            ListIterator iterator = user.getSettings().listIterator();
            while (iterator.hasNext()) {
                UserSetting s = (UserSetting) iterator.next();
                if (s == setting) {
                    iterator.remove();
                }
            }
            return remove;
        } else {
            user.getSettings().add(setting);
            return add;
        }
    }

    protected static final String[] NAMES = new String[]{"UserSettings"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "allows setting of certain user settings.\n" + settings(command);
    }

    protected static final String USAGE = "[@User] [Setting]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
