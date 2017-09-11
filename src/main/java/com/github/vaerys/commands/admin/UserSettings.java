package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.EnumString;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings implements Command {
    private String settings = "**Settings**\n" +
            "> DeniedXp\n" +
            "> DontShowRank\n" +
            "> DenyMakeCC\n" +
            "> DenyUseCCs\n" +
            "> AutoShitpost\n" +
            "> DenyAutoRole\n" +
            "> List - `Shows the user's settings.`";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, split.getFirstWord(), false);
        if (user == null) {
            return "> Could not find user.";
        }
        ProfileObject profile = user.getProfile(command.guild);
        if (profile != null) {
            String toTest = "";
            if (split.getRest() != null) {
                toTest = split.getRest().toLowerCase();
            }
            switch (toTest) {
                case "deniedxp":
                    return toggleSetting(profile, UserSetting.DENIED_XP,
                            "> **" + user.displayName + "** will now gain xp again.",
                            "> **" + user.displayName + "** will no longer gain XP.");
                case "dontshowrank":
                    return toggleSetting(profile, UserSetting.DONT_SHOW_LEADERBOARD,
                            "> **" + user.displayName + "'s** rank is now visible.",
                            "> **" + user.displayName + "** will no longer show their rank.");
                case "denymakecc":
                    return toggleSetting(profile, UserSetting.DENY_MAKE_CC,
                            "> **" + user.displayName + "** can now make custom commands.",
                            "> **" + user.displayName + "** can no longer make custom commands.");
                case "denyuseccs":
                    return toggleSetting(profile, UserSetting.DENY_USE_CCS,
                            "> **" + user.displayName + "** can now use custom commands.",
                            "> **" + user.displayName + "** can no longer use custom commands.");
                case "autoshitpost":
                    return toggleSetting(profile, UserSetting.AUTO_SHITPOST,
                            "> **" + user.displayName + "** no longer has the shitpost tag automatically added to all of their new Custom Commands.",
                            "> **" + user.displayName + "** now has the shitpost tag automatically added to all of their new Custom Commands.");
                case "denyautorole":
                    return toggleSetting(profile, UserSetting.DENY_AUTO_ROLE,
                            "> **" + user.displayName + "** will no longer automatically be granted roles.",
                            "> **" + user.displayName + "** will now automatically be granted roles.");
                case "dontdecay":
                    return toggleSetting(profile, UserSetting.DONT_DECAY,
                            "> **" + user.displayName + "** will now have pixel decay.",
                            "> **" + user.displayName + "** will no longer have pixel decay.");
                case "list":
                    List<String> userSettings = new ArrayList<>();
                    for (UserSetting s : profile.getSettings()) {
                        userSettings.add(EnumString.get(s));
                    }
                    XEmbedBuilder builder = new XEmbedBuilder();
                    builder.withTitle(user.displayName + "'s User settings:");
                    if (userSettings.size() == 0) {
                        return "> **" + user.displayName + "** has no settings on their profile.";
                    } else {
                        builder.withDesc("```\n" + Utility.listFormatter(userSettings, true) + "```");
                    }
                    builder.withColor(command.client.color);
                    Utility.sendEmbedMessage("", builder, command.channel.get());
                    return "";
                default:
                    return settings + "\n\n" + Utility.getCommandInfo(this, command);
            }
        } else {
            return "> Invalid user.";
        }
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

    @Override
    public String[] names() {
        return new String[]{"UserSettings"};
    }

    @Override
    public String description() {
        return "allows setting of certain user settings.\n" + settings;
    }

    @Override
    public String usage() {
        return "[@User] [Setting]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
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
        return true;
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
