package com.github.vaerys.main;

import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.BlackListObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.TagType;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    //Logger
    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    //Discord Utils
    public static IRole getRoleFromName(String roleName, IGuild guild, boolean startsWith) {
        IRole role = null;
        for (IRole r : guild.getRoles()) {
            if (startsWith) {
                if (r.getName().toLowerCase().startsWith(roleName.toLowerCase())) {
                    role = r;
                }
            } else {
                if (r.getName().equalsIgnoreCase(roleName)) {
                    role = r;
                }
            }
        }
        return role;
    }

    public static IRole getRoleFromName(String roleName, IGuild guild) {
        return getRoleFromName(roleName, guild, false);
    }

    public static boolean testForPerms(IUser user, IGuild guild, Permissions... perms) {
        if (perms.length == 0) return true;
        if (guild == null) return true;
        if (canBypass(user, guild)) return true;
        EnumSet<Permissions> toMatch = EnumSet.noneOf(Permissions.class);
        toMatch.addAll(Arrays.asList(perms));
//        Debug code.
        List<String> toMatchList = new ArrayList<String>() {{
            addAll(toMatch.stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        List<String> userList = new ArrayList<String>() {{
            addAll(user.getPermissionsForGuild(guild).stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        if (true) {
            logger.trace("To Match : " + Utility.listFormatter(toMatchList, true));
            logger.trace("User Perms : " + Utility.listFormatter(userList, true));
            logger.trace("Result : " + user.getPermissionsForGuild(guild).containsAll(toMatch));
        }
//        end Debug
        return user.getPermissionsForGuild(guild).containsAll(toMatch);
    }

    public static boolean testForPerms(CommandObject object, Permissions... perms) {
        return testForPerms(object.user.get(), object.guild.get(), perms);
    }

    public static boolean testForPerms(UserObject user, GuildObject guild, Permissions... perms) {
        return testForPerms(user.get(), guild.get(), perms);
    }

    public static boolean testForPerms(CommandObject command, IChannel channel, Permissions... perms) {
        boolean hasPerms = true;
        if (canBypass(command.user.get(), command.guild.get())) {
            return true;
        }
        for (Permissions p : perms) {
            if (!channel.getModifiedPermissions(command.user.get()).contains(p)) {
                hasPerms = false;
            }
        }
        return hasPerms;
    }

    //Command Utils
    public static String getCommandInfo(Command command, CommandObject commandObject) {
        StringBuilder response = new StringBuilder(">> **" + commandObject.guild.config.getPrefixCommand() + command.names[0]);
        if (command.usage != null) {
            response.append(" " + command.usage);
        }
        response.append("** <<");
        return response.toString();
    }

    public static String getCommandInfo(Command command) {
        StringBuilder response = new StringBuilder(">> **" + Globals.defaultPrefixCommand + command.names[0]);
        if (command.usage != null) {
            response.append(" " + command.usage);
        }
        response.append("** <<");
        return response.toString();
    }

    public static String checkBlacklist(String message, List<BlackListObject> blacklist) {
        for (BlackListObject b : blacklist) {
            if (message.toLowerCase().contains(b.getPhrase().toLowerCase())) {
                return b.getReason();
            }
        }
        return null;
    }

    //File Utils
    public static String getFilePath(long guildID, String type) {
        return Constants.DIRECTORY_STORAGE + guildID + "/" + type;
    }

    public static String getFilePath(long guildID, String type, boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/" + type;
    }

    public static String getDirectory(long guildID) {
        return Constants.DIRECTORY_STORAGE + guildID + "/";
    }

    public static String getGuildImageDir(long guildID) {
        return getDirectory(guildID) + Constants.DIRECTORY_GUILD_IMAGES;
    }

    public static String getDirectory(long guildID, boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/";
    }

    public static String removeMentions(String from) {
        if (from == null) {
            return from;
        }
        return from.replaceAll("(?i)@everyone", "").replaceAll("(?i)@here", "");
    }


    public static Color getUsersColour(IUser user, IGuild guild) {
        //before
        List<IRole> userRoles = guild.getRolesForUser(user);
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return null;
    }

    public static Color getUsersColour(List<IRole> userRoles, IGuild guild) {
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return Color.black;
    }

    //Time Utils
    public static String formatTime(long timeSeconds, boolean readable) {
        long days = 0;
        if (readable) {
            days = TimeUnit.SECONDS.toDays(timeSeconds);
            timeSeconds -= TimeUnit.DAYS.toSeconds(days);
        }
        long hours = TimeUnit.SECONDS.toHours(timeSeconds);
        timeSeconds -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(timeSeconds);
        timeSeconds -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(timeSeconds);
        String time;
        if (readable) {
            StringBuilder msg = new StringBuilder();
            int values = 0;
            if (days != 0) values++;
            if (hours != 0) values++;
            if (minutes != 0) values++;
            if (seconds != 0) values++;
            if (days != 0) {
                msg.append(days + " day");
                addSplit(msg, values, days);
                values--;
            }
            if (hours != 0) {
                msg.append(hours + " hour");
                addSplit(msg, values, hours);
                values--;
            }
            if (minutes != 0) {
                msg.append(minutes + " minute");
                addSplit(msg, values, minutes);
                values--;
            }
            if (seconds != 0) {
                msg.append(seconds + " second");
                addSplit(msg, values, seconds);
                values--;
            }
            time = msg.toString();
        } else {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return time;
    }

    private static StringBuilder addSplit(StringBuilder msg, int values, long time) {
        if (time > 1) msg.append("s");
        if (values == 2) msg.append(" and ");
        if (values > 2) msg.append(", ");
        return msg;
    }

    public static String formatTimeSeconds(long timeSeconds) {
        return formatTime(timeSeconds, false);
    }


    public static Boolean testModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "+":
                return true;
            case "-":
                return false;
            case "add":
                return true;
            case "del":
                return false;
            default:
                return null;
        }
    }

    public static boolean canBypass(IUser author, IGuild guild, boolean logging) {
        GuildConfig config = Globals.getGuildContent(guild.getLongID()).config;
        if (author.getLongID() == Globals.creatorID && config.debugMode) {
            if (logging) {
                logger.trace("User is Creator, BYPASSING.");
            }
            return true;
        }
        if (guild == null) {
            return false;
        }
        if (author.getLongID() == guild.getOwnerLongID()) {
            if (logging) {
                logger.trace("User is Guild Owner, GUILD : \"" + guild.getLongID() + "\", BYPASSING.");
            }
            return true;
        }
        if (author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }

    public static boolean canBypass(IUser author, IGuild guild) {
        return canBypass(author, guild, true);
    }

    public static boolean canBypass(UserObject user, GuildObject guild) {
        return canBypass(user.get(), guild.get());
    }

    public static long getMentionUserID(String content) {
        if (content.contains("<@")) {
            long userID = stringLong(StringUtils.substringBetween(content, "<@!", ">"));
            if (userID == -1) {
                userID = stringLong(StringUtils.substringBetween(content, "<@", ">"));
            }
            IUser user = Globals.getClient().getUserByID(userID);
            if (user != null) {
                return userID;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static String convertMentionToText(String from) {
        String last;
        if (from == null || from.isEmpty()) {
            return from;
        }
        do {
            last = from;
            if (from.contains("<@") || from.contains("<!@")) {
                long userID = getMentionUserID(from);
                if (userID != -1) {
                    IUser mentioned = Globals.getClient().getUserByID(userID);
                    from = from.replace("<@!" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                    from = from.replace("<@" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                }
            }
        } while (!last.equals(from));
        return from;
    }

    public static void listFormatterEmbed(String title, XEmbedBuilder builder, List<String> list,
                                          boolean horizontal) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`", false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```", false);
        }
    }

    public static void listFormatterEmbed(String title, EmbedBuilder builder, List<String> list,
                                          boolean horizontal, String suffix) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer + suffix, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`\n" + suffix, false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```\n" + suffix, false);
        }
    }

    public static String listFormatter(List<String> list, boolean singleLine) {
        StringBuilder formattedList = new StringBuilder();
        if (list.size() == 0) {
            return "";
        }
        if (singleLine) {
            for (String s : list) {
                formattedList.append(s).append(", ");
            }
            formattedList.delete(formattedList.length() - 2, formattedList.length());
            formattedList.append(".");
            return formattedList.toString();
        } else {
            for (String s : list) {
                formattedList.append(s).append("\n");
            }
            return formattedList.toString();
        }
    }
    
    public static String listEnumFormatter(List<? extends Enum<?>> list, boolean singleLine) {
        return listFormatter(EnumListToStringList(list), singleLine);
    }
    
    public static List<String> EnumListToStringList(List<? extends Enum<?>> list) {
        Stream<? extends Enum<?>> EnumStream = list.stream();
        List<String> lst = EnumStream.map(Enum::toString).collect(Collectors.toList());
        EnumStream.close();
        return lst;
    }

    public static List<IRole> getRolesByName(IGuild guild, String name) {
        List<IRole> roles = guild.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return roles;
    }

    public static void sendGlobalAdminLogging(Command command, String args, CommandObject commandObject) {
        for (GuildObject c : Globals.getGuilds()) {
            StringHandler message = new StringHandler("***GLOBAL LOGGING***\n> **@")
                    .append(commandObject.user.username)
                    .append("** Has Used Command `")
                    .append(command.names[0]).append("`");
            List<IChannel> adminlog = c.getChannelsByType(ChannelSetting.ADMIN_LOG);
            List<IChannel> serverLog = c.getChannelsByType(ChannelSetting.SERVER_LOG);

            IChannel channel = null;

            if (!(args == null || args.isEmpty())) {
                message.append(" with args: `" + args + "`");
            }
            if (serverLog.size() != 0) {
                channel = serverLog.get(0);
            }
            if (adminlog.size() != 0) {
                channel = adminlog.get(0);
            }
            if (channel != null) {
                RequestHandler.sendMessage(message.toString(), channel);
            }
        }
    }

    public static String formatTimeDifference(long difference) {
        StringHandler formatted = new StringHandler();
        try {
            long days = TimeUnit.SECONDS.toDays(difference);
            long hours = TimeUnit.SECONDS.toHours(difference);
            hours -= days * 24;
            long mins = TimeUnit.SECONDS.toMinutes(difference);
            mins -= (days * 24 + hours) * 60;

            if (days > 0) {
                if (days > 1) {
                    formatted.append(days).append(" days, ");
                } else {
                    formatted.append(days).append(" day, ");
                }
            }
            if (hours > 0) {
                if (hours > 1) {
                    formatted.append(hours).append(" hours and ");
                } else {
                    formatted.append(hours).append(" hour and ");
                }
            }
            if (mins > 1) {
                formatted.append(mins).append(" minutes ago");
            } else if (mins != 0) {
                formatted.append(mins).append(" minute ago");
            }
            if (difference < 60) {
                formatted.setContent("less than a minute ago");
            }
        } catch (NoSuchElementException e) {
            logger.error("Error getting Edited Message Timestamp.");
        }
        return formatted.toString();
    }

    public static String removeFun(String from) {
        String last;
        boolean exit;
        do {
            last = from;
            exit = false;
            if (from.contains("***")) {
                from = replaceFun(from, "***");
                exit = true;
            }
            if (from.contains("**") && !exit) {
                from = replaceFun(from, "**");
                exit = true;
            }
            if (from.contains("*") && !exit) {
                from = replaceFun(from, "*");
            }
            exit = false;
            if (from.contains("```")) {
                from = replaceFun(from, "```");
                exit = true;
            }
            if (from.contains("`") && !exit) {
                from = replaceFun(from, "`");
            }
            exit = false;
            if (from.contains("~~")) {
                from = replaceFun(from, "~~");
            }
            if (from.contains("__")) {
                from = replaceFun(from, "__");
                exit = true;
            }
            if (from.contains("_") && !exit) {
                from = replaceFun(from, "_");
            }
        } while (last != from);
        return from;
    }

    public static String replaceFun(String from, String fun) {
        String noFun = StringUtils.substringBetween(from, fun, fun);
        if (noFun != null) {
            from = from.replace(fun + noFun + fun, noFun);
        }
        return from;
    }

    public static String truncateString(String str, int maxLength, boolean truncateAtSpace) {
        String result = str;

        if (str.length() >= maxLength) {
            int endI = maxLength;
            if (truncateAtSpace) {
                // want to truncate the message at the last valid space
                // see if we can't find it.
                if (str.substring(0, maxLength).lastIndexOf(' ') != -1) {
                    endI = str.substring(0, maxLength).lastIndexOf(' ');
                } else {
                    // was not able to find a space
                    endI = maxLength;
                }
            }
            result = str.substring(0, endI) + "...";
        }

        return result;
    }


    public static String truncateString(String str, int maxLength) {
        return truncateString(str, maxLength, true);
    }

    public static boolean isImageLink(String link) {
        if (!checkURL(link)) {
            return false;
        }
        List<String> suffixes = new ArrayList<String>() {{
            add(".png");
            add(".gif");
            add(".jpg");
            add(".webp");
            add(".jpeg");
        }};
        if (link.contains("\n") || link.contains(" ")) {
            return false;
        }
        for (String s : suffixes) {
            if (link.toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean testUserHierarchy(IUser higherUser, IUser lowerUser, IGuild guild) {
        List<IRole> lowerRoles = lowerUser.getRolesForGuild(guild);
        List<IRole> higherRoles = higherUser.getRolesForGuild(guild);
        IRole topRole = null;
        int topRolePos = 0;
        for (IRole role : higherRoles) {
            if (topRole == null) {
                topRole = role;
                topRolePos = role.getPosition();
            } else {
                if (role.getPosition() > topRolePos) {
                    topRole = role;
                    topRolePos = role.getPosition();
                }
            }
        }
        for (IRole role : lowerRoles) {
            if (role.getPosition() > topRolePos) {
                return false;
            }
        }
        return true;
    }

    public static boolean testUserHierarchy(UserObject higherUser, UserObject lowerUser, GuildObject guild) {
        if (canBypass(lowerUser.get(), guild.get())) return false;
        return testUserHierarchy(higherUser.get(), lowerUser.get(), guild.get());
    }

    public static boolean testUserHierarchy(IUser author, IRole toTest, IGuild guild) {
        boolean roleIsLower = false;
        for (IRole r : author.getRolesForGuild(guild)) {
            if (toTest.getPosition() < r.getPosition()) {
                roleIsLower = true;
            }
        }
        return roleIsLower;
    }

    public static long textToSeconds(String time) {
        try {
            String sub = time.substring(0, time.length() - 1);
            long timeSecs = Long.parseLong(sub);
            if (time.toLowerCase().endsWith("s")) {
                return timeSecs;
            } else if (time.toLowerCase().endsWith("m")) {
                return timeSecs * 60;
            } else if (time.toLowerCase().endsWith("h")) {
                return timeSecs * 60 * 60;
            } else if (time.toLowerCase().endsWith("d")) {
                return timeSecs * 60 * 60 * 24;
            } else {
                timeSecs = Long.parseLong(time);
                return timeSecs;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void sendLog(String content, GuildObject guild, boolean isAdmin, EmbedObject... object) {
        IChannel logChannel = null;
        List<IChannel> serverLog = guild.getChannelsByType(ChannelSetting.SERVER_LOG);
        List<IChannel> adminLog = guild.getChannelsByType(ChannelSetting.ADMIN_LOG);
        if (isAdmin) {
            if (adminLog.size() != 0) {
                logChannel = adminLog.get(0);
            }
        } else {
            if (serverLog.size() != 0) {
                logChannel = serverLog.get(0);
            }
        }
        if (logChannel == null) {
            return;
        } else if (object.length == 0) {
            RequestHandler.sendMessage(content, logChannel);
        } else {
            RequestHandler.sendEmbed(content, object[0], logChannel);
        }
    }

    public static String embedToString(EmbedObject embed) {
        if (embed == null) return "";
        StringHandler embedToString = new StringHandler();
        if (embed.author != null) embedToString.append("**").append(embed.author.name).append("**\n");
        if (embed.title != null) embedToString.append("**").append(embed.title).append("**\n");
        if (embed.description != null) embedToString.append(embed.description).append("\n");
        if (embed.fields != null) {
            for (EmbedObject.EmbedFieldObject field : embed.fields) {
                embedToString.append("**").append(field.name).append("**\n").append(field.value).append("\n");
            }
        }
        if (embed.footer != null) embedToString.append("*").append(embed.footer.text).append("*");
        if (embed.image != null) embedToString.append("\n").append(embed.image.url);
        return embedToString.toString();
    }

    public static String unFormatMentions(IMessage message) {
        StringHandler from = new StringHandler(message.getContent());
        from.replaceRegex("(?i)(@here|@everyone)", "**[REDACTED]**");
        for (IUser user : message.getMentions()) {
            if (user != null) {
                StringHandler regex = new StringHandler("<@!?").append(user.getLongID()).append(">");
                StringHandler replacement = new StringHandler("__@").append(user.getDisplayName(message.getGuild())).append("__");
                from.replaceRegex(regex, replacement);
            }
        }
        for (IRole role : message.getRoleMentions()) {
            StringHandler toReplace = new StringHandler("<@&").append(role.getLongID()).append(">");
            StringHandler replaceWith = new StringHandler("__**@").append(role.getName()).append("**__");
            from.replace(toReplace, replaceWith);
        }
        return from.toString();
    }

    public static String formatTimestamp(ZonedDateTime time) {
        StringHandler content = new StringHandler();
        content.append(time.getYear());
        content.append("/").append(time.getMonthValue());
        content.append("/").append(time.getDayOfMonth());
        content.append(" - ").append(time.getHour());
        content.append(":").append(time.getMinute());
        content.append(":").append(time.getSecond());
        return content.toString();
    }

    public static boolean checkURL(String args) {
        List<String> blacklist = Globals.getBlacklistedURls();
        for (String s : blacklist) {
            SplitFirstObject firstWord = new SplitFirstObject(s);
            if (!firstWord.getFirstWord().startsWith("//")) {
                if (firstWord.getFirstWord() != null && firstWord.getFirstWord().length() != 0) {
                    if (args.toLowerCase().contains(firstWord.getFirstWord().toLowerCase())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void sortRewards(List<RewardRoleObject> rewardRoles) {
        Collections.sort(rewardRoles, (o1, o2) -> {
            if (o1.getLevel() < o2.getLevel()) {
                return -1;
            } else if (o1.getLevel() > o2.getLevel()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public static void sortUserObjects(List<ProfileObject> users, boolean sortAsc) {
        if (sortAsc) {
            Collections.sort(users, (o1, o2) -> {
                if (o1.getXP() < o2.getXP()) {
                    return -1;
                } else if (o1.getXP() > o2.getXP()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        } else {
            Collections.sort(users, (o1, o2) -> {
                if (o1.getXP() > o2.getXP()) {
                    return -1;
                } else if (o1.getXP() < o2.getXP()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    public static List<String> getChannelMentions(ArrayList<Long> channelIDs, CommandObject command) {
        List<String> channelNames = new ArrayList<>();
        if (channelIDs != null) {
            for (long s : channelIDs) {
                IChannel channel = command.guild.getChannelByID(s);
                if (channel != null) {
                    channelNames.add(channel.mention());
                }
            }
        }
        return channelNames;
    }

    public static void sendStack(Exception e) {
        StringBuffer s = new StringBuffer(ExceptionUtils.getStackTrace(e));
        s.append(s.substring(0, s.length() - 2));
        if (!s.toString().endsWith(")")) {
            s.append(")");
        }
        StringHandler builder = new StringHandler();
        builder.addViaJoin(Globals.getAllLogs(), "\n");
        logger.error(s.toString() + "\n>> LAST " + Globals.getAllLogs().size() + " DEBUG LOGS<<\n" + builder.toString());
    }

    public static List<Command> getCommandsByType(List<Command> commands, CommandObject commandObject, SAILType type, boolean testPerms) {
        List<Command> toReturn = new ArrayList<>();
        for (Command c : commands) {
            SAILType ctype = c.type;
            if (c.channel != null && c.channel == ChannelSetting.FROM_DM) {
                ctype = SAILType.DM;
            }
            if (ctype == type) {
                if (testPerms) {
                    if (c.type == SAILType.CREATOR && commandObject.user.longID != commandObject.client.creator.longID) {
                        //do nothing
                    } else if (testForPerms(commandObject, c.perms)) {

                        toReturn.add(c);
                    }
                } else {
                    toReturn.add(c);
                }
            }
        }
        toReturn.sort(Comparator.comparing(o -> o.names[0]));
        return toReturn;
    }

    public static List<String> getChannelMentions(List<IChannel> channels) {
        List<String> mentions = new ArrayList<>();
        for (IChannel c : channels) {
            mentions.add(c.mention());
        }
        return mentions;
    }

    public static UserObject getUser(CommandObject command, String args, boolean doContains, boolean hasProfile) {
        if (args != null && !args.isEmpty()) {
            IUser user = null;
            IUser conUser = null;
            String toTest;
            if (args.split(" ").length != 1) {
                toTest = escapeRegex(args);
            } else {
                toTest = escapeRegex(args).replace("_", "[_| ]");
            }
            List<IUser> guildUsers = command.guild.getUsers();
            guildUsers.sort(Comparator.comparing(o -> o.getRolesForGuild(command.guild.get()).size()));
            Collections.reverse(guildUsers);
            for (IUser u : guildUsers) {
                if (user != null) {
                    break;
                }
                try {
                    UserObject object = new UserObject(u, command.guild, true);

                    if (hasProfile) {
                        ProfileObject profile = object.getProfile(command.guild);
                        if (profile == null || profile.isEmpty()) {
                            throw new IllegalStateException("Profile Is Null");
                        }
                    }
                    if ((u.getName() + "#" + u.getDiscriminator()).matches("(?i)" + toTest)) {
                        user = u;
                    }
                    if (u.getName().matches("(?i)" + toTest) && user == null) {
                        user = u;
                    }
                    String displayName = u.getDisplayName(command.guild.get());
                    if (displayName.matches("(?i)" + toTest) && user == null) {
                        user = u;
                    }
                    if (doContains && conUser == null) {
                        if (u.getName().matches("(?i).*" + toTest + ".*")) {
                            conUser = u;
                        }
                        if (displayName.matches("(?i).*" + toTest + ".*") && conUser == null) {
                            conUser = u;
                        }
                    }
                } catch (PatternSyntaxException e) {
                    //continue.
                } catch (IllegalStateException e) {
                    //continue.
                }
            }
            try {
                long uID = Long.parseLong(args);
                user = command.client.get().getUserByID(uID);
            } catch (NumberFormatException e) {
                if (command.message.get().getMentions().size() > 0) {
                    user = command.message.get().getMentions().get(0);
                }
            }
            if (user == null && doContains) {
                user = conUser;
            }
            if (user != null) {
                UserObject userObject = new UserObject(user, command.guild);
                return userObject;
            }
        }
        return null;
    }

    public static UserObject getUser(CommandObject command, String args, boolean doContains) {
        return getUser(command, args, doContains, true);
    }

    public static String escapeRegex(String args) {
        //[\^$.|?*+(){}
        StringHandler replace = new StringHandler(args);
        replace.replace("\\", "\\u005C");
        replace.replace("[", "\\u005B");
        replace.replace("^", "\\u005E");
        replace.replace("$", "\\u0024");
        replace.replace(".", "\\u002E");
        replace.replace("|", "\\u007C");
        replace.replace("?", "\\u003F");
        replace.replace("*", "\\u002A");
        replace.replace("+", "\\u002B");
        replace.replace("(", "\\u0028");
        replace.replace(")", "\\u0029");
        replace.replace("{", "\\u007B");
        replace.replace("}", "\\u007D");
        return replace.toString();
    }

    public static long stringLong(String string) {
        try {
            return Long.parseUnsignedLong(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public static ReactionEmoji getReaction(String emojiName) {
        Emoji emoji = EmojiManager.getForAlias(emojiName);
        if (emoji == null) {
            throw new IllegalStateException("Invalid unicode call: " + emojiName);
        }
        return ReactionEmoji.of(emoji.getUnicode());
    }


    public static boolean canBypass(CommandObject command) {
        return canBypass(command.user.get(), command.guild.get());
    }

    public static String prepArgs(String args) {
        StringHandler replace = new StringHandler(args);
        replace.replace("{", "<u007B>");
        replace.replace("}", "<u007D>");
        replace.replace("(", "<u0028>");
        replace.replace(")", "<u0029>");
        replace.replace(":", "<u003A>");
        replace.replace(";", "<u003B>");
        return replace.toString();
    }

    public static String removePrep(String args) {
        StringHandler replace = new StringHandler(args);
        replace.replace("<u007B>", "{");
        replace.replace("<u007D>", "}");
        replace.replace("<u0028>", "(");
        replace.replace("<u0029>", ")");
        replace.replace("<u003A>", ":");
        replace.replace("<u003B>", ";");
        return replace.toString();
    }

    public static String getDateSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String getStringClosest(List<String> list, String toTest, boolean startsWith, boolean ignoreCase) {
        float topPercentage = 0;
        String mostCorrect = null;
        for (String s : list) {
            char[] toTestChars;
            char[] stringChars;
            if (ignoreCase) toTestChars = toTest.toLowerCase().toCharArray();
            else toTestChars = toTest.toCharArray();

            if (ignoreCase) stringChars = s.toLowerCase().toCharArray();
            else stringChars = s.toCharArray();

            float correctChars = 0;
            for (int i = 0; i < toTest.length(); i++) {
                try {
                    boolean valueMatches = toTestChars[i] == stringChars[i];
                    if (startsWith && !valueMatches) {
                        break;
                    } else if (valueMatches) {
                        correctChars++;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }
            float testLength = toTest.length();
            float percentMatches = correctChars / testLength * 100;
            if (percentMatches > topPercentage) {
                topPercentage = percentMatches;
                mostCorrect = s;
            }
        }
        return mostCorrect;
    }

    public static String getChannelMessage(List<String> channelMentions) {
        if (channelMentions.size() == 0) {
            return "> You do not have access to any channels that you are able to run this command in.";
        } else if (channelMentions.size() == 1) {
            return "> Command must be performed in: " + channelMentions.get(0);
        } else {
            return "> Command must be performed in any of the following channels: \n" + Utility.listFormatter(channelMentions, true);
        }
    }

    public static boolean isImgurAlbum(String fileURL) {
        return Pattern.compile("https?://imgur\\.com/a/.*").matcher(fileURL).matches();
    }

    public static String getUnicodeEmoji(String emojiName) {
        Emoji emoji = EmojiManager.getForAlias(emojiName);
        if (emoji == null) {
            throw new IllegalStateException("Invalid unicode call: " + emojiName);
        }
        return emoji.getUnicode();
    }

    public static String formatError(Object object) {
        return "at " + object.getClass().getName() + "(" + object.getClass().getSimpleName() + ".java:0)\n";
    }

    public static long getRepeatTimeValue(StringHandler reason) {
        try {
            String timeString = reason.split(" ")[0];
            long timeSecs = 0;
            List<String> list = new ArrayList<>();
            for (String s : reason.toString().split(" ")) {
                if (Pattern.compile("(?i)[0-9]*(s|m|h|d)").matcher(s).matches()) {
                    list.add(s);
                } else {
                    break;
                }
            }
            if (list.size() == 0) {
                timeSecs = Long.parseLong(timeString) * 60;
                list.add(timeString);
            } else {
                for (String s : list) {
                    timeSecs += Utility.textToSeconds(s);
                }
            }
            reason.replaceOnce(String.join(" ", list), "");
            if (reason.startsWith(" ")) {
                reason.replaceOnce(" ", "");
            }
            return timeSecs;
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}

//    public static List<String> getAlbumIUrls(String fileURL) {
//        String id = getImgurID(fileURL);
//        if (id == null) return new ArrayList<>();
//        Album album = getAlbum(id);
//        if (album != null) {
//            return album.getImages().stream().map(image -> image.getLink()).collect(Collectors.toList());
//        } else return new ArrayList<>();
//    }

//    private static String getImgurID(String fileURL) {
//        if (!isImgurAlbum(fileURL)) return null;
//        return fileURL.replaceAll("https?://imgur\\.com/a/", "");
//    }

//    public static Album getAlbum(String id) {
//        String albulUrl = "https://api.imgur.com/3/album/";
//        albulUrl += id;
//        HttpURLConnection request;
//        try {
//            URL url = new URL(albulUrl);
//            request = (HttpURLConnection) url.openConnection();
//            request.setDoOutput(true);
//            request.setRequestMethod("GET");
//            request.connect();
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(new InputStreamReader((org.omg.CORBA.portable.InputStream) request.getContent()));
//            if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Album album = new Gson().fromJson(element.getAsJsonObject().getAsString(), Album.class);
//                return album;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public static HttpResponse requestPOST(String requestURL, StringEntity entity) {
//        String postUrl = requestURL;// put in your url
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost post = new HttpPost(postUrl);
//        post.setEntity(entity);
//        post.setHeader("Content-type", "application/json");
//        try {
//            return httpClient.execute(post);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
