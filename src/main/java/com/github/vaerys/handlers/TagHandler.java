package com.github.vaerys.handlers;

import com.github.vaerys.annotations.TagAnnotation;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ReplaceObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Vaerys on 22/10/2016.
 */
public class TagHandler {

    // TODO: 12/11/2016 replace repetitive code for the IfTags
    // TODO: 15/01/2017 actually use the annotations

    public static String tagSystem(String contents, CommandObject object, String args) {
        String response = contents;
        response = tagArgs(response, args);
        response = tagSpacer(response);
        response = tagNoNL(response);
        response = tagGetAuthor(response, object.user.get(), object.guild.get());
        response = tagGetUsername(response, object.user.get());
        response = tagSpecialArgs(response, args);
        response = tagRandom(response);
        response = tagReplaceRandom(response);
        response = tagIfRole(response, object.user.get(), object.guild.get());
        response = tagIfName(response, object.user.get(), object.guild.get());
        response = tagEmptyArgs(response, args);
        response = tagIfArgs(response, args);
        response = tagReplace(response, "<replace>{", "}", ";;");
        response = tagReplace(response, "<replace!>(", ")</r>", "::");
        response = tagRegex(response);
        response = tagRandNum(response);
        response = tagRandEmote(response, object.guild);
        response = replaceError(response);
        response = tagToCaps(response);
        response = tagMentionToString(response, object);
        return response;
    }

    private static String tagReplaceRandom(String from) {
        String prefix = "<replaceRandom>{";
        String suffix = "}";
        String splitter = ";;";
        String tagRegex;
        String lastAttempt;
        ArrayList<ReplaceObject> toReplace = new ArrayList<>();
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tagRegex = StringUtils.substringBetween(from, prefix, suffix);
                if (tagRegex != null) {
                    ArrayList<String> splitRegex = new ArrayList<>(Arrays.asList(tagRegex.split(splitter)));
                    String toRegex = prefix + tagRegex + suffix;
                    if (splitRegex.size() >= 2) {
                        from = from.replace(toRegex, "");
                        Random random = new Random();
                        int randomNum = random.nextInt(splitRegex.size() - 1);
                        toReplace.add(new ReplaceObject(splitRegex.get(0), splitRegex.get(randomNum + 1)));
                    } else {
                        from = from.replace(tagRegex, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
            for (ReplaceObject r : toReplace) {
                from = from.replace(r.getFrom(), r.getTo());
            }
        }
        return from;
    }

    private static String tagRegex(String from) {
        String prefix = "<regex>{";
        String suffix = "}</r>";
        String splitter = ";;";
        String tagRegex;
        String lastAttempt;
        ArrayList<ReplaceObject> toReplace = new ArrayList<>();
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tagRegex = StringUtils.substringBetween(from, prefix, suffix);
                if (tagRegex != null) {
                    ArrayList<String> splitRegex = new ArrayList<>(Arrays.asList(tagRegex.split(splitter)));
                    String toRegex = prefix + tagRegex + suffix;
                    if (splitRegex.size() == 2) {
                        from = from.replace(toRegex, "");
                        toReplace.add(new ReplaceObject(splitRegex.get(0), splitRegex.get(1)));
                    } else {
                        from = from.replace(tagRegex, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
            for (ReplaceObject r : toReplace) {
                try {
                    from = from.replaceAll(r.getFrom(), r.getTo());
                } catch (PatternSyntaxException e) {
                    from = from.replace(r.getFrom(), "#ERROR#");
                }
            }
        }
        return from;
    }

    private static String tagMentionToString(String response, CommandObject object) {

        while (response.matches(".*<@!?[0-9]*>.*")){
            String id = StringUtils.substringBetween(response,"<@!",">");
            if (id == null){
                id = StringUtils.substringBetween(response,"<@",">");
            }
            try {
                long userID = Long.parseUnsignedLong(id);
                IUser user = object.guild.getUserByID(userID);
                if (user != null){
                    response = response.replace(user.mention(true), user.getDisplayName(object.guild.get()));
                    response = response.replace(user.mention(false), user.getDisplayName(object.guild.get()));
                }else {
                    throw new NumberFormatException("You shouldn't see this.");
                }
            }catch (NumberFormatException e){
                response = response.replace("<@!" + id + ">", "null");
                response = response.replace("<@" + id + ">", "null");
            }
        }


        return response;
    }

    private static String tagRandEmote(String from, GuildObject guild) {
        String tagCall = "<randEmote>";
        String lastAttempt;
        if (from.contains(tagCall)) {
            do {
                lastAttempt = from;
                Random rand = new Random();
                List<IEmoji> emojis = guild.get().getEmojis();
                if (emojis.size() == 0) {
                    return from;
                }
                IEmoji randomEmoji = emojis.get(rand.nextInt(emojis.size()));
                if (randomEmoji != null) {
                    from = from.replaceFirst(tagCall, randomEmoji.toString());
                }
            } while (StringUtils.countMatches(from, tagCall) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    private static String replaceError(String from) {
        String tag;
        String prefix = "<repError>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    from = from.replace("#ERROR#", tag);
                    from = from.replace(prefix + tag + suffix, "");
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    public static String getTagList(String type) {
        Method[] methods = TagHandler.class.getMethods();
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            for (Method m : methods) {
                if (m.isAnnotationPresent(TagAnnotation.class)) {
                    TagAnnotation anno = m.getAnnotation(TagAnnotation.class);
                    if (anno.priority() == i) {
                        if (anno.type().equalsIgnoreCase(type) || anno.type().equalsIgnoreCase(Constants.TAG_TYPE_ALL)) {
                            tags.add("**" + anno.usage() + "**\n" + Constants.PREFIX_INDENT + "`" + anno.description() + "`");
                        }
                    }
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are all of the Tags you can use.\n");
        for (String s : tags) {
            builder.append(Constants.PREFIX_INDENT + s + "\n");
        }
        return builder.toString();
    }

    @TagAnnotation(name = "Author", description = "Replaces this tag with the author's Display name.", usage = "#author#", priority = 9)
    private static String tagGetAuthor(String from, IUser author, IGuild guild) {
        return from.replace("<author>", author.getDisplayName(guild));
    }

    @TagAnnotation(name = "UserName", description = "Replaces this tag with the author's Display name.", usage = "#username#", priority = 9)
    private static String tagGetUsername(String from, IUser author) {
        return from.replace("<username>", author.getName());
    }

    @TagAnnotation(name = "Args", description = "This tag replaces the tag with anything after the custom command.", usage = "#args#", priority = 1)
    public static String tagArgs(String from, String args) {
        return from.replace("<args>", args);
    }

    @TagAnnotation(name = "Args!", description = "This tag replaces with the word with the position of the number in the tag's arguments.", usage = "#args!#{[Position]}", priority = 2)
    public static String tagSpecialArgs(String from, String args) {
        String tag;
        String prefix = "<args!>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    try {
                        int position = Integer.parseInt(tag);
                        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
                        if (splitArgs.size() == 0) {
                            from = from.replace(prefix + tag + suffix, "");
                        } else if (splitArgs.size() < position) {
                            from = from.replace(prefix + tag + suffix, "");
                        } else if (splitArgs.get(position - 1) == null) {
                            from = from.replace(prefix + tag + suffix, "");
                        } else {
                            from = from.replace(prefix + tag + suffix, splitArgs.get(position - 1));
                        }
                    } catch (NumberFormatException e) {
                        from = from.replace(prefix + tag + suffix, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "Random", description = "This tag creates a random output.", usage = "#random#{[Rand 1];;[Rand 2]...}", priority = 3,
            type = Constants.TAG_TYPE_CC)
    public static String tagRandom(String from) {
        String tagRandom;
        String prefixRandom = "<random>{";
        String suffixRandom = "}";
        String lastAttempt;
        if (from.contains(prefixRandom)) {
            do {
                lastAttempt = from;
                tagRandom = StringUtils.substringBetween(from, prefixRandom, suffixRandom);
                if (tagRandom != null) {
                    ArrayList<String> splitRandom = new ArrayList<>(Arrays.asList(tagRandom.split(";;")));
                    Random random = new Random();
                    String toRegex = prefixRandom + tagRandom + suffixRandom;
                    from = from.replaceFirst(Pattern.quote(toRegex), splitRandom.get(random.nextInt(splitRandom.size())));
                }
            } while (StringUtils.countMatches(from, prefixRandom) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "Replace", description = "This tag replaces all of the first with the second.", usage = "#replace#{from;;to} or #!replace#(from::to)#!r#", priority = 4,
            type = Constants.TAG_TYPE_CC)
    public static String tagReplace(String from, String prefix, String suffix, String splitter) {
        String tagRegex;
        String lastAttempt;
        ArrayList<ReplaceObject> toReplace = new ArrayList<>();
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tagRegex = StringUtils.substringBetween(from, prefix, suffix);
                if (tagRegex != null) {
                    ArrayList<String> splitRegex = new ArrayList<>(Arrays.asList(tagRegex.split(splitter)));
                    String toRegex = prefix + tagRegex + suffix;
                    if (splitRegex.size() == 2) {
                        from = from.replace(toRegex, "");
                        toReplace.add(new ReplaceObject(splitRegex.get(0), splitRegex.get(1)));
                    } else {
                        from = from.replace(tagRegex, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
            for (ReplaceObject r : toReplace) {
                from = from.replace(r.getFrom(), r.getTo());
            }
        }
        return from;
    }

    @TagAnnotation(name = "IfRole", description = "This tag lets you choose a response based on whether the user has a role or not.", usage = "#ifRole#{[RoleName];;[ResponseTrue];;[ResponseFalse]}", priority = 3,
            type = Constants.TAG_TYPE_CC)
    public static String tagIfRole(String from, IUser author, IGuild guild) {
        String tag;
        String prefix = "<ifRole>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    ArrayList<String> splitString = new ArrayList<>(Arrays.asList(tag.split(";;")));
                    String toRegex = prefix + tag + suffix;
                    if (splitString.size() == 3) {
                        boolean hasRole = false;
                        for (IRole r : author.getRolesForGuild(guild)) {
                            if (r.getName().toLowerCase().contains(splitString.get(0).toLowerCase())) {
                                hasRole = true;
                            }
                        }
                        if (hasRole) {
                            from = from.replace(toRegex, splitString.get(1));
                        } else {
                            from = from.replace(toRegex, splitString.get(2));
                        }
                    } else {
                        from = from.replace(tag, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "IfName", description = "This tag lets you choose a response based on the user's display name.", usage = "#ifName#{[Name];;[ResponseTrue];;[ResponseFalse]}", priority = 3,
            type = Constants.TAG_TYPE_CC)
    public static String tagIfName(String from, IUser author, IGuild guild) {
        String tag;
        String prefix = "<ifName>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    ArrayList<String> splitString = new ArrayList<>(Arrays.asList(tag.split(";;")));
                    String toRegex = prefix + tag + suffix;
                    if (splitString.size() == 3) {
                        if (author.getDisplayName(guild).toLowerCase().contains(splitString.get(0).toLowerCase())) {
                            from = from.replace(toRegex, splitString.get(1));
                        } else {
                            from = from.replace(toRegex, splitString.get(2));
                        }
                    } else {
                        from = from.replace(tag, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "Spacer", description = "Creates a blank character in place of this tag, the blank character does not take up space.", usage = "#spacer#")
    public static String tagSpacer(String from) {
        return from.replace("<spacer>", "\u200b");
    }

    @TagAnnotation(name = "NoBreak", description = "Removes a \\n from the command if you put this tag at the end of the line.", usage = "#!break#")
    public static String tagNoNL(String from) {
        return from.replace("<!break>\n", "");
    }

    @TagAnnotation(name = "IfArgs", description = "Replaces the tag with a certain response based if #args# contains something or not.", usage = "#ifArgs#{[Test];;[ResponseTrue];;[ResponseFalse]}", priority = 3)
    public static String tagIfArgs(String from, String args) {
        String tag;
        String prefix = "<ifArgs>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    ArrayList<String> splitString = new ArrayList<>(Arrays.asList(tag.split(";;")));
                    String toRegex = prefix + tag + suffix;
                    if (splitString.size() == 3) {
                        if (args.toLowerCase().contains(splitString.get(0).toLowerCase())) {
                            from = from.replace(toRegex, splitString.get(1));
                        } else {
                            from = from.replace(toRegex, splitString.get(2));
                        }
                    } else {
                        from = from.replace(tag, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    public static String tagEmptyArgs(String from, String args) {
        String prefix = "<ifArgsEmpty>{";
        String suffix = "}";
        String tag;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    ArrayList<String> splitString = new ArrayList<>(Arrays.asList(tag.split(";;")));
                    String toRegex = prefix + tag + suffix;
                    if (splitString.size() == 2) {
                        if (args == null || args.isEmpty()) {
                            from = from.replace(toRegex, splitString.get(0));
                        } else {
                            from = from.replace(toRegex, splitString.get(1));
                        }
                    } else {
                        from = from.replace(tag, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "RandNum", description = "Replaces the tag with a random number between the first number and the second number(inclusive).", usage = "#randNum#{[RandMin];;[RandMax]}", priority = 5)
    public static String tagRandNum(String from) {
        String tag;
        String prefix = "<randNum>{";
        String suffix = "}";
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                tag = StringUtils.substringBetween(from, prefix, suffix);
                if (tag != null) {
                    ArrayList<String> splitString = new ArrayList<>(Arrays.asList(tag.split(";;")));
                    if (splitString.size() == 2) {
                        try {
                            long randMin = Long.parseLong(tag.split(";;")[0]);
                            long randMax = Long.parseLong(tag.split(";;")[1]);
                            long randomNumber = ThreadLocalRandom.current().nextLong(randMin, randMax + 1);
                            from = from.replaceFirst(Pattern.quote(prefix + tag + suffix), randomNumber + "");
                        } catch (NumberFormatException e) {
                            from = from.replace(prefix + tag + suffix, "#ERROR#");
                        } catch (IllegalArgumentException e) {
                            from = from.replace(prefix + tag + suffix, "#ERROR#");
                        }
                    } else {
                        from = from.replace(tag, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    public static String tagMentionRemover(String from) {
        from = from.replaceAll("(?i)@everyone", "**[REDACTED]**");
        from = from.replaceAll("(?i)@here", "**[REDACTED]**");
        return from;
    }

    public static String testForShit(String from) {
        return from.replace("<shitpost>", "");
    }

    public static String testForLock(String from, IUser author, IGuild guild) {
        if (Utility.testForPerms(author, guild, Permissions.MANAGE_MESSAGES)) {
            return from.replace("<lock>", "");
        }
        return from;
    }

    @TagAnnotation(name = "Channel", description = "Replaces tag with channel mention of the same ID", usage = "#channel#{[ChannelID]}", type = Constants.TAG_TYPE_INFO)
    public static String tagChannel(String from) {
        String prefix = "<channel>{";
        String suffix = "}";
        long contents;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                contents = Utility.stringLong(StringUtils.substringBetween(from, prefix, suffix));
                if (contents != -1) {
                    try {
                        IChannel channel = Globals.getClient().getChannelByID(contents);
                        if (channel != null) {
                            from = from.replace(prefix + contents + suffix, channel.mention());
                        } else {
                            from = from.replaceFirst(Pattern.quote(contents + ""), "#ERROR#");
                        }
                    } catch (NumberFormatException e) {
                        from = from.replaceFirst(Pattern.quote(contents + ""), "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "DisplayName", description = "Replaces tag with DisplayName of the user with the same ID", usage = "#displayName#{[UserID]}", type = Constants.TAG_TYPE_INFO)
    public static String tagDisplayName(String from, IGuild guild) {
        String prefix = "<displayName>{";
        String suffix = "}";
        long contents;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                contents = Utility.stringLong(StringUtils.substringBetween(from, prefix, suffix));
                if (contents != -1) {
                    try {
                        IUser user = Globals.getClient().getUserByID(contents);
                        if (user != null) {
                            from = from.replace(prefix + contents + suffix, user.getDisplayName(guild));
                        } else {
                            from = from.replaceFirst(Pattern.quote(contents + ""), "#ERROR#");
                        }
                    } catch (NumberFormatException e) {
                        from = from.replaceFirst(Pattern.quote(contents + ""), "#ERROR#");
                    }

                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    public static String tagEmoji(String from, IGuild guild) {
        String prefix = "#:";
        String suffix = ":#";
        String contents;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                contents = StringUtils.substringBetween(from, prefix, suffix);
                if (contents != null) {
                    IEmoji emoji = guild.getEmojiByName(contents);
                    if (emoji != null) {
                        from = from.replace(prefix + contents + suffix, emoji.toString());
                    } else {
                        from = from.replaceFirst(Pattern.quote(contents), "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    public static String tagEmbedImage(String from, String prefix) {
        String tag;
        String suffix = "}";
        if (from.contains(prefix)) {
            tag = StringUtils.substringBetween(from, prefix, suffix);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }

    public static IUser tagUser(String args) {
        String prefix = "<user>{";
        String suffix = "}";
        long contents;
        if (args.contains(prefix)) {
            contents = Utility.stringLong(StringUtils.substringBetween(args, prefix, suffix));
            if (contents != -1) {
                IUser user = Globals.getClient().getUserByID(contents);
                if (user != null) {
                    return user;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else return null;
    }

    public static String tagToCaps(String from) {
        String tag = "<toCaps>";
        String toEmbed = "<embedImage>{";
        if (from.contains(tag) && !from.contains(toEmbed)) {
            return from.replace(tag, "").toUpperCase();
        }
        return from;
    }

    public static String prepDailyMessage(String content, GuildObject guild) {
        content = tagRandEmote(content, guild);
        content = tagSpacer(content);
        content = tagRandom(content);
        content = tagNoNL(content);
        content = tagMentionRemover(content);
        return content;
    }

    public static StringBuilder prepLevelUpMessage(StringBuilder levelUpMessage, GuildObject guild, UserObject user) {
        levelUpMessage = new StringBuilder(levelUpMessage.toString().replace("<level>", user.getProfile(guild).getCurrentLevel() + ""));
        levelUpMessage = new StringBuilder(levelUpMessage.toString().replace("<user>", user.get().mention()));
        levelUpMessage = new StringBuilder(tagRandEmote(levelUpMessage.toString(), guild));
        return levelUpMessage;
    }
}
