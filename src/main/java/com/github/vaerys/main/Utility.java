package com.github.vaerys.main;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.objects.depreciated.BlackListObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    private Utility() {
        throw new IllegalStateException("Utility Class");
    }

    //Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);

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
        if (isBackup) {
            return Constants.DIRECTORY_BACKUPS + guildID + "/" + type;
        } else return getFilePath(guildID, type);
    }

    public static String getDirectory(long guildID) {
        return Constants.DIRECTORY_STORAGE + guildID + "/";
    }

    public static String getGuildImageDir(long guildID) {
        return getDirectory(guildID) + Constants.DIRECTORY_GUILD_IMAGES;
    }

    public static String getDirectory(long guildID, boolean isBackup) {
        if (isBackup) {
            return Constants.DIRECTORY_BACKUPS + guildID + "/";
        } else return getDirectory(guildID);
    }

    public static String removeMentions(String from) {
        if (from == null) return null;
        from = from.replaceAll("<@&[0-9]*>", "");
        return from.replaceAll("(?i)@(everyone|here)", "");
    }


    //Time Utils
    public static String formatTime(long timeSeconds, boolean readable) {
        if (timeSeconds == 0) return "0 seconds";
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
                msg.append(days).append(" day");
                addSplit(msg, values, days);
                values--;
            }
            if (hours != 0) {
                msg.append(hours).append(" hour");
                addSplit(msg, values, hours);
                values--;
            }
            if (minutes != 0) {
                msg.append(minutes).append(" minute");
                addSplit(msg, values, minutes);
                values--;
            }
            if (seconds != 0) {
                msg.append(seconds).append(" second");
                addSplit(msg, values, seconds);
            }
            time = msg.toString();
        } else {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return time;
    }

    private static void addSplit(StringBuilder msg, int values, long time) {
        if (time > 1) msg.append("s");
        if (values == 2) msg.append(" and ");
        if (values > 2) msg.append(", ");
    }

    public static String formatTimeSeconds(long timeSeconds) {
        return formatTime(timeSeconds, false);
    }


    public static Boolean testModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "+":
            case "add":
                return true;
            case "-":
            case "del":
                return false;
            default:
                return null;
        }
    }

    public static long getMentionUserID(String content) {
        if (content.contains("<@")) {
            long userID = stringLong(StringUtils.substringBetween(content, "<@!", ">"));
            if (userID == -1) {
                userID = stringLong(StringUtils.substringBetween(content, "<@", ">"));
            }
            User user = Client.getClientObject().getUserByID(userID);
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
                    User mentioned = Client.getClient().getUserById(userID);
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
            title = Command.SPACER;
        }
        if (formattedList.isEmpty()) {
            builder.addField(title, Command.SPACER, false);
            return;
        }
        if (horizontal) {
            builder.addField(title, "`" + formattedList + "`", false);
        } else {
            builder.addField(title, "```\n" + formattedList + "```", false);
        }
    }

    public static void listFormatterEmbed(String title, EmbedBuilder builder, List<String> list, boolean horizontal, String suffix) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.SPACER;
        }
        if (formattedList.isEmpty()) {
            builder.addField(title, Command.SPACER + suffix, false);
            return;
        }
        if (horizontal) {
            builder.addField(title, "`" + formattedList + "`\n" + suffix, false);
        } else {
            builder.addField(title, "```\n" + formattedList + "```\n" + suffix, false);
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

    public static void sendGlobalAdminLogging(Command command, String args, GlobalUserObject user) {
        StringHandler message = new StringHandler("***GLOBAL LOGGING***\n> **@%s** Has Used Command `%s`",
                user.username, command.names[0]);
        for (GuildObject c : Globals.getGuilds()) {
            if (!c.config.moduleLogging) continue;
            if (!c.config.adminLogging) continue;

            List<TextChannel> adminlog = c.getChannelsByType(ChannelSetting.ADMIN_LOG);
            List<TextChannel> serverLog = c.getChannelsByType(ChannelSetting.SERVER_LOG);

            TextChannel channel = null;

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
                channel.sendMessage(message.toString()).queue();
            }
        }
    }


    /***
     * Formats seconds to plain text.
     *
     * @param difference time in seconds
     * @return time unit to plain text.
     */
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
            LOGGER.error("Error getting Edited Message Timestamp.");
        }
        return formatted.toString();
    }

    public static String removeFun(String from) {
        String last;
        boolean[] exit = new boolean[]{false};
        do {
            exit[0] = false;
            last = from;
            from = replaceFun(from, "```", exit);
            if (!exit[0]) from = replaceFun(from, "`", exit);
            if (!exit[0]) from = replaceFun(from, "~~", exit);
            if (!exit[0]) from = replaceFun(from, "__", exit);
            if (!exit[0]) from = replaceFun(from, "_", exit);
            from = from.replace("*", "");
        } while (last != from);
        return from;
    }

    public static String escapeFun(String from) {

        from = from.replace("`", "\\`");
        from = from.replace("*", "\\*");
        from = from.replace("_", "\\_");
        return from.replace("~", "\\~");
    }

    public static String replaceFun(String from, String fun, boolean[] exit) {
        String noFun = StringUtils.substringBetween(from, fun, fun);
        if (noFun != null) {
            from = from.replace(escapeRegex(fun), "");
            exit[0] = true;
        }
        return from;
    }

    /**
     * Shortens a string based on passed parameters. A string that is successfully truncated will have "..." appended to
     * the end of the string.
     *
     * @param str             The string that will be shortened.
     * @param maxLength       The longest length you want the string to be.
     * @param truncateAtSpace Set to true to have the method attempt to find the first valid whitespace character
     *                        to use as the position to truncate the text.
     * @param search          The number of characters backwards to search. This value is ignored if truncateAtSpace is false.
     * @return The string passed to str will be returned as-is if the truncation was not successful, otherwise it will
     * be shortened and have "..." appended to the end.
     */
    public static String truncateString(String str, int maxLength, boolean truncateAtSpace, int search) {
        String result = str;

        if (str.length() > maxLength) {
            int endI = maxLength;
            if (truncateAtSpace) {
                String subStr = str.substring((maxLength - search), maxLength);
                endI = subStr.lastIndexOf(' ');
                if (endI == -1) {
                    endI = maxLength;
                } else {
                    endI += (maxLength - search);
                }
            }
            result = str.substring(0, endI) + "...";
        }

        return result;
    }


    public static String truncateString(String str, int maxLength) {
        int search = maxLength <= 10 ? maxLength : 10;

        return truncateString(str, maxLength, true, search);
    }

    public static boolean isImageLink(String link, boolean isSendURL) {
        if (!checkURL(link)) {
            return false;
        }
        try {
            if (isSendURL) new URL(link);
        } catch (MalformedURLException e) {
            return false;
        }
        List<String> suffixes = new ArrayList<>();
        suffixes.add(".png");
        suffixes.add(".gif");
        suffixes.add(".jpg");
        suffixes.add(".webp");
        suffixes.add(".jpeg");
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

    public static boolean isImageLink(String link) {
        return isImageLink(link, false);
    }


    /***
     * Tests the role hierarchy of two users.
     *
     * @param higherUser the globalUser that will be performing the action
     * @param lowerUser the globalUser that the action will performed on
     * @param guild the guild the action should take place
     * @return if the higher globalUser is above the lower globalUser
     */
    public static boolean testUserHierarchy(Member higherUser, Member lowerUser, Guild guild, boolean sameLevel) {
        List<Role> lowerRoles = lowerUser.getRoles();
        List<Role> higherRoles = higherUser.getRoles();
        // higher globalUser is guild owner, automatically has highest role:
        if (guild.getOwner().equals(higherUser)) return true;
        Role topRole = null;
        int topRolePos = 0;
        for (Role role : higherRoles) {
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
        for (Role role : lowerRoles) {
            if (sameLevel) {
                if (role.getPosition() > topRolePos) {
                    return false;
                }
            } else {
                if (role.getPosition() >= topRolePos) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean testUserHierarchy(Member higherUser, Member lowerUser, Guild guild) {
        return testUserHierarchy(higherUser, lowerUser, guild, true);
    }

    /***
     * Tests the role hierarchy of two users.
     *
     * @param higherUser the globalUser that will be performing the action
     * @param lowerUser the globalUser that the action will performed on
     * @param guild the guild the action should take place
     * @return if the higher globalUser is above the lower globalUser
     */
    public static boolean testUserHierarchy(UserObject higherUser, UserObject lowerUser, GuildObject guild) {
        if (GuildHandler.canBypass(lowerUser.getMember(), guild.get())) return false;
        return testUserHierarchy(higherUser.getMember(), lowerUser.getMember(), guild.get());
    }

    public static boolean testUserHierarchy(Member author, Role toTest, Guild guild) {
        boolean roleIsLower = false;
        for (Role r : author.getRoles()) {
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

    public static String embedToString(MessageEmbed embed) {
        if (embed == null) return "";
        StringHandler embedToString = new StringHandler();
        if (embed.getAuthor() != null) embedToString.append("**").append(embed.getAuthor().getName()).append("**\n");
        if (embed.getTitle() != null) embedToString.append("**").append(embed.getTitle()).append("**\n");
        if (embed.getDescription() != null) embedToString.append(embed.getDescription()).append("\n");
        for (MessageEmbed.Field field : embed.getFields()) {
            embedToString.append("**").append(field.getName()).append("**\n").append(field.getValue()).append("\n");
        }
        if (embed.getFooter() != null) {
            embedToString.append("*").append(embed.getFooter().getText()).append("*");
            if (embed.getTimestamp() != null) embedToString.append(" | ");
        }
        if (embed.getThumbnail() != null) {
            embedToString.append(embed.getTimestamp());
        }
        if (embed.getImage() != null) embedToString.append("\n").append(embed.getImage().getUrl());
        return embedToString.toString();
    }

    public static String unFormatMentions(Message message) {
        StringHandler from = new StringHandler(message.getContentRaw());
        from.replaceRegex("(?i)(@here|@everyone)", "**[REDACTED]**");
        for (User user : message.getMentionedUsers()) {
            if (user != null) {
                StringHandler regex = new StringHandler("<@!?").append(user.getIdLong()).append(">");
                StringHandler replacement;
                if (message.getChannel().getType().isGuild()) {
                    Member member = message.getGuild().getMember(user);
                    replacement = new StringHandler("__@").append(member == null ? user.getName() : member.getEffectiveName()).append("__");
                } else {
                    replacement = new StringHandler("__@").append(user.getName()).append("__");
                }
                from.replaceRegex(regex, replacement);
            }
        }
        for (Role role : message.getMentionedRoles()) {
            StringHandler toReplace = new StringHandler("<@&").append(role.getIdLong()).append(">");
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
        rewardRoles.sort(Comparator.comparingLong(RewardRoleObject::getLevel));
    }

    public static void sortUserObjects(List<ProfileObject> users, boolean sortAsc) {
        if (sortAsc) {
            users.sort(Comparator.comparingLong(ProfileObject::getXP));
        } else {
            users.sort((o1, o2) -> Long.compare(o2.getXP(), o1.getXP()));
        }
    }

    public static List<String> getChannelMentions(List<Long> channelIDs, CommandObject command) {
        List<String> channelNames = new ArrayList<>();
        if (channelIDs != null) {
            for (long s : channelIDs) {
                TextChannel channel = command.guild.getChannelByID(s);
                if (channel != null) {
                    channelNames.add(channel.getAsMention());
                }
            }
        }
        return channelNames;
    }


    public static void sendStack(Exception e) {
        sendStack(e, 5);
    }

    public static void sendStack(Exception e, int count) {
        StringHandler s = new StringHandler(ExceptionUtils.getStackTrace(e));
        s.setContent(s.substring(0, s.length() - 2));
        if (!s.toString().endsWith(")")) {
            s.append(")");
        }
        if (count > 25) count = 25;
        StringHandler builder = new StringHandler();
        builder.addViaJoin(Globals.getAllLogs().subList(0, count), "\n");
        LOGGER.error(s.toString() + "\n>> LAST " + Globals.getAllLogs().size() + " DEBUG LOGS<<\n" + builder.toString());
    }

    public static List<Command> getCommandsByType(List<Command> commands, CommandObject commandObject, SAILType type, boolean testPerms) {
        List<Command> toReturn = new ArrayList<>();

        //return empty if not creator.
        if (type == SAILType.CREATOR && !commandObject.user.checkIsCreator()) return toReturn;

        //get command list
        switch (type) {
            case DM:
                toReturn.addAll(commands.stream()
                        .filter(c -> c.channel == ChannelSetting.FROM_DM)
                        .filter(c -> {
                            //only show creator commands if the globalUser the creator.
                            if (c.type == SAILType.CREATOR && commandObject.user.checkIsCreator())
                                return true;
                            if (c.type != SAILType.CREATOR) {
                                return true;
                            }
                            return false;
                        })
                        .collect(Collectors.toList()));
                return toReturn;
            default:
                toReturn.addAll(commands.stream()
                        .filter(c -> c.type == type).collect(Collectors.toList()));
                break;
        }

        toReturn.addAll(commands.stream().filter(c -> c.subCommands.size() != 0)
                .filter(c -> c.hasSubCommands(type))
                .collect(Collectors.toList()));

        if (testPerms) {
            toReturn = toReturn.stream()
                    .filter(c -> c.isVisibleInType(commandObject, type))
                    .collect(Collectors.toList());
        }
        toReturn = toReturn.stream().filter(c -> c.channel != ChannelSetting.FROM_DM).collect(Collectors.toList());
        toReturn.sort(Comparator.comparing(o -> o.names[0]));
        return toReturn;
    }


    public static List<String> getChannelMentions(List<TextChannel> channels) {
        return channels.stream().map(IMentionable::getAsMention).collect(Collectors.toList());
    }

    public static UserObject getUser(CommandObject command, String args, boolean doContains, boolean hasProfile) {
        if (args == null || args.isEmpty()) return null;
        try {
            long userId = Long.parseUnsignedLong(args);
            User user = command.client.getUserByID(userId);
            if (user != null || UserObject.checkForUser(userId, command.guild)) {
                return new UserObject(user, command.guild);
            }
        } catch (NumberFormatException e) {
            List<User> mention = command.message.getMentions();
            if (!mention.isEmpty()) {
                Collections.reverse(mention);
                return new UserObject(mention.get(0), command.guild);
            }
        }


        Member user = null;
        Member conUser = null;
        String toTest;
        if (args.split(" ").length != 1) {
            toTest = escapeRegex(args);
        } else {
            toTest = escapeRegex(args).replace("_", "[_| ]");
        }
        List<Member> guildUsers = command.guild.getUsers();
        guildUsers.sort(Comparator.comparing(o -> o.getRoles().size()));
        Collections.reverse(guildUsers);
        for (Member u : guildUsers) {
            if (user != null) {
                break;
            }
            try {
                UserObject object = new UserObject(u, command.guild);
                if (hasProfile) {
                    ProfileObject profile = object.getProfile();
                    if (profile == null || profile.isEmpty()) continue;
                }
                if ((u.getUser().getAsTag()).matches("(?i)" + toTest)) {
                    user = u;
                }
                if (u.getUser().getName().matches("(?i)" + toTest) && user == null) {
                    user = u;
                }
                String displayName = u.getEffectiveName();
                if (displayName.matches("(?i)" + toTest) && user == null) {
                    user = u;
                }
                if (doContains && conUser == null) {
                    if (u.getUser().getName().matches("(?i).*" + toTest + ".*")) {
                        conUser = u;
                    }
                    if (displayName.matches("(?i).*" + toTest + ".*") && conUser == null) {
                        conUser = u;
                    }
                }
            } catch (PatternSyntaxException e) {
                //continue.
            }
        }
        UserObject userObject = null;
        if (user == null && doContains) {
            user = conUser;
        }
        if (user != null) {
            userObject = new UserObject(user, command.guild);
        }
        return userObject;
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

    public static String getDefaultAvatarURL(long userID) {
        return String.format("https://cdn.discordapp.com/embed/avatars/%d.png", new Random(userID).nextInt(5));
    }


    public static String prepArgs(String args) {
        StringHandler replace = new StringHandler(args);
        replace.replace(">", "<u003E>");
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
        replace.replace("<u003E>", ">");
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

    public static String getChannelMessage(List<TextChannel> channels) {
        List<String> channelMentions = Utility.getChannelMentions(channels);
        if (channels.size() == 0) {
            return "\\> You do not have access to any channels that you are able to run this command in.";
        } else if (channels.size() == 1) {
            return "\\> Command must be performed in: " + channelMentions.get(0);
        } else {
            return "\\> Command must be performed in any of the following channels: \n" + Utility.listFormatter(channelMentions, true);
        }
    }

//    public static boolean isImgurAlbum(String fileURL) {
//        return Pattern.compile("https?://imgur\\.com/a/.*").matcher(fileURL).matches();
//    }

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

    public static String enumToString(Enum<?> e) {
        String enumValue = e.toString();
        enumValue = enumValue.toLowerCase();
        enumValue = enumValue.replace("_", " ");
        String[] words = enumValue.split(" ");
        StringHandler fixedEnum = new StringHandler();
        for (String s : words) {
            if (fixedEnum.toString().length() != 0) {
                fixedEnum.append(" ");
            }
            fixedEnum.append(StringUtils.capitalize(s));
        }
        return fixedEnum.toString();
    }


}
