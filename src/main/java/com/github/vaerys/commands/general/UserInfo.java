package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class UserInfo extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user;
        if (args == null || args.isEmpty()) {
            user = command.user;
        } else {
            user = Utility.getUser(command, args, true);
        }
        if (user == null) {
            return "> Could not find user.";
        }
        ProfileObject profile = user.getProfile(command.guild);
        if (profile == null && user.get().isBot()) {
            if (user.get().getPresence().getStatus().equals(StatusType.OFFLINE) || user.get().getPresence().getStatus().equals(StatusType.UNKNOWN)) {
                return "> Could not get a profile for " + user.displayName + ".";
            }
            profile = new ProfileObject(user.longID);
            command.guild.users.addUser(profile);
        } else if (profile == null) {
            return "> Could not get a profile for " + user.displayName + ".";
        }
        if (!GuildHandler.testForPerms(command, Permissions.ADMINISTRATOR) &&
                (user.isPrivateProfile(command.guild) && user.longID != command.user.longID)) {
            return "> " + user.displayName + " has set their profile to private.";
        }

        //start of the profile builder.
        XEmbedBuilder builder = new XEmbedBuilder(user);
        List<IRole> roles = user.roles;
        ArrayList<String> roleNames = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        //If user is a bot it will display the image below as the user avatar icon.
        if (user.get().isBot()) {
            builder.withAuthorIcon("http://i.imgur.com/aRJpAP4.png");
        }
        //sets title to user Display Name;


        builder.withAuthorName(user.displayName);


        //sets thumbnail to user Avatar.
        builder.withThumbnail(user.get().getAvatarURL());

        //gets the age of the account.
        long nowUTC = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        ZonedDateTime creationDate = user.get().getCreationDate().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        long creationUTC = creationDate.toEpochSecond();

        long difference = nowUTC - creationUTC;

        //collect role names;
        roleNames.addAll(roles.stream().filter(role -> !role.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));

        if (profile.getLinks() != null && profile.getLinks().size() > 0) {
            links.addAll(profile.getLinks().stream().map(link -> link.toString()).collect(Collectors.toList()));
        }

        //builds desc
        StringBuilder desc = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        if (profile.getSettings().contains(UserSetting.READ_RULES) && command.guild.config.readRuleReward) {
            builder.withFooterIcon(Constants.STICKER_STAR_URL);
//            builder.withFooterIcon("https://emojipedia-us.s3.amazonaws.com/thumbs/120/twitter/120/glowing-star_1f31f.png");
        }
        if (command.guild.config.userInfoShowsDate) {
            builder.withTimestamp(user.get().getCreationDate());
            footer.append("UserID: " + profile.getUserID() + ", Creation Date");
//            desc.append("**Account Created: **" + creationDate.format(formatter));
        } else {
            desc.append("**Account Created: **" + Utility.formatTimeDifference(difference));
            footer.append("User ID: " + profile.getUserID());
        }
        builder.withFooterText(footer.toString());
        desc.append("\n**Gender: **" + profile.getGender());

        boolean showLevel = true;
        boolean showCC = command.guild.config.moduleCC;

        if (!command.guild.config.modulePixels || profile.getXP() == 0) {
            showLevel = false;
        }

        if (showCC && !showLevel) {
            desc.append("\n**Custom Commands: **" + command.guild.customCommands.getUserCommandCount(user, command.guild));
        } else if (showLevel && !showCC) {
            desc.append("\n**Level: **" + XpHandler.xpToLevel(profile.getXP()));
        } else if (showLevel && showCC) {
            desc.append("\n**Custom Commands: **" + command.guild.customCommands.getUserCommandCount(user, command.guild) +
                    indent + indent + indent + "**Level: **" + XpHandler.xpToLevel(profile.getXP()));
        }

        desc.append("\n**Roles: **" + Utility.listFormatter(roleNames, true));
        desc.append("\n\n*" + profile.getQuote() + "*");
        desc.append("\n" + Utility.listFormatter(links, true));

        if (user.isPatron) {
            builder.withAuthorIcon(Constants.PATREON_ICON_URL);
        }

        builder.withDesc(desc.toString());
//        builder.withFooterText("User ID: " + profile.getUserID());

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
