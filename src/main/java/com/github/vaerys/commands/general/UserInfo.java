package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class UserInfo extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user;
        if (args == null || args.isEmpty()) user = command.user;
        else user = Utility.getUser(command, args, true);
        if (user == null) return "> Could not find user.";

        ProfileObject profile = user.getProfile(command.guild);

        //init bot profile
        if (profile == null && user.get().isBot()) {
            profile = new ProfileObject(user.longID);
            command.guild.users.addUser(profile);
        } else if (profile == null) {
            return "> Could not get a profile for " + user.displayName + ".";
        }

        //private profile check
        if (!GuildHandler.testForPerms(command, Permissions.ADMINISTRATOR) &&
                (user.isPrivateProfile(command.guild) && user.longID != command.user.longID)) {
            return "> " + user.displayName + " has set their profile to private.";
        }

        //start of the profile builder.
        XEmbedBuilder builder = new XEmbedBuilder(user);
        List<IRole> roles = user.roles;
        List<String> roleNames = roles.stream().filter(role -> !role.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList());
        List<String> links = profile.getLinks().stream().map(link -> link.toString()).collect(Collectors.toList());
        long accountAge = user.getAccountAgeSeconds();
        boolean showCC = command.guild.config.moduleCC;
        boolean showLevel = command.guild.config.modulePixels && profile.getXP() != 0;
        StringBuilder desc = new StringBuilder();

        //sets title to user Display Name;
        builder.withAuthorName(user.displayName);

        //sets thumbnail to user Avatar.
        builder.withThumbnail(user.avatarURL);

        //set author Icon
        if (user.longID == 153159020528533505L || user.longID == 175442602508812288L) {
            builder.withAuthorIcon(Constants.DEV_IMAGE_URL);
            builder.withAuthorUrl(Constants.LINK_GITHUB);
        } else if (user.isPatron) builder.withAuthorIcon(Constants.PATREON_ICON_URL);
        //bots only
        if (user.isBot) builder.withAuthorIcon(Constants.BOT_USER_URL);
        if (user.get() == null) {
            builder.withAuthorIcon(Constants.UNKNOWN_USER_URL);
        }

        //append sticker
        if (profile.getSettings().contains(UserSetting.READ_RULES) && command.guild.config.readRuleReward) {
            builder.withFooterIcon(Constants.STICKER_STAR_URL);
        }

        //build desc desc
        if (command.guild.config.userInfoShowsDate) {
            builder.withTimestamp(user.creationDate);
            builder.withFooterText("UserID: " + profile.getUserID() + ", Creation Date");
        } else {
            desc.append("**Account Created: **" + Utility.formatTimeDifference(accountAge));
            builder.withFooterText("User ID: " + profile.getUserID());
        }

        //append gender
        desc.append("\n**Gender: **" + profile.getGender());

        //append cc count and level
        if (showCC || showLevel) desc.append("\n");
        if (showCC) desc.append(String.format("**Custom Commands:** %d", user.customCommands.size()));
        if (showLevel) {
            if (showCC) desc.append(indent + indent + indent);
            desc.append(String.format("**Level:** %d", PixelHandler.xpToLevel(profile.getXP())));
        }

        //append roles
        if (roleNames.size() != 0) desc.append("\n**Roles: **" + Utility.listFormatter(roleNames, true));
        desc.append("\n\n");

        //append quote
        if (!profile.getQuote().isEmpty()) desc.append("*" + profile.getQuote() + "*");
        //append links
        if (links.size() != 0) desc.append("\n").append(Utility.listFormatter(links, true));
        //add break between footer
        desc.append("\n").append(spacer);
        //add desc
        builder.withDesc(desc.toString());
        //sends Message
        if (user.getProfile(command.guild).getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            RequestHandler.sendEmbedMessage("", builder, command.user.get().getOrCreatePMChannel());
            return "> Profile sent to your Direct messages.";
        }
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Profile", "UserInfo", "Me"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lets you see some information about yourself or another user.";
    }

    @Override
    protected String usage() {
        return "(@user)";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.PROFILES;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
