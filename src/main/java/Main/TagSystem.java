package Main;

import Annotations.TagAnnotation;
import Objects.ReplaceObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 22/10/2016.
 */
public class TagSystem {

    // TODO: 12/11/2016 replace repetitive code for the IfTags
    // TODO: 15/01/2017 actually use the annotations

    public static String tagSystem(String contents, IMessage message, String args) {
        String response = contents;
        response = tagArgs(response, args);
        response = tagNoNL(response);
        response = tagGetAuthor(response, message.getAuthor(), message.getGuild());
        response = tagGetUsername(response, message.getAuthor());
        response = tagSpacer(response);
        response = tagSpecialArgs(response, args);
        response = tagRandom(response);
        response = tagIfRole(response, message.getAuthor(), message.getGuild());
        response = tagIfName(response, message.getAuthor(), message.getGuild());
        response = tagIfArgs(response, args);
        response = tagRegex(response, "#replace#{", "}", ";;");
        response = tagRegex(response, "#replace!#(", ")#!r#", "::");
        response = tagRandNum(response);
        response = tagMentionRemover(response);
        return response;
    }

    public static String getTagList(String type) {
        Method[] methods = TagSystem.class.getMethods();
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
        return from.replace("#author#", author.getDisplayName(guild));
    }

    @TagAnnotation(name = "UserName", description = "Replaces this tag with the author's Display name.", usage = "#username#", priority = 9)
    private static String tagGetUsername(String from, IUser author) {
        return from.replace("#username#", author.getName());
    }

    @TagAnnotation(name = "Args", description = "This tag replaces the tag with anything after the custom command.", usage = "#args#", priority = 1)
    public static String tagArgs(String from, String args) {
        return from.replace("#args#", args);
    }

    @TagAnnotation(name = "Args!", description = "This tag replaces with the word with the position of the number in the tag's arguments.", usage = "#args!#{[Position]}", priority = 2)
    public static String tagSpecialArgs(String from, String args) {
        String tag;
        String prefix = "#args!#{";
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
        String prefixRandom = "#random#{";
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
    public static String tagRegex(String from, String prefix, String suffix, String splitter) {
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
        String prefix = "#ifRole#{";
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
                        if (author.getRolesForGuild(guild).toString().toLowerCase().contains(splitString.get(0).toLowerCase())) {
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
        String prefix = "#ifName#{";
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
                        if (author.getDisplayName(guild).toString().toLowerCase().contains(splitString.get(0).toLowerCase())) {
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
        return from.replace("#spacer#", "\u200b");
    }

    @TagAnnotation(name = "NoBreak", description = "Removes a \\n from the command if you put this tag at the end of the line.", usage = "#!break#")
    public static String tagNoNL(String from) {
        return from.replace("#!break#\n", "");
    }

    @TagAnnotation(name = "IfArgs", description = "Replaces the tag with a certain response based if #args# contains something or not.", usage = "#ifArgs#{[Test];;[ResponseTrue];;[ResponseFalse]}", priority = 3)
    public static String tagIfArgs(String from, String args) {
        String tag;
        String prefix = "#ifArgs#{";
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
                        if (splitString.get(0).equalsIgnoreCase(args)) {
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

    @TagAnnotation(name = "RandNum", description = "Replaces the tag with a random number between the first number and the second number(inclusive).", usage = "#randNum#{[RandMin];;[RandMax]}", priority = 5)
    public static String tagRandNum(String from) {
        String tag;
        String prefix = "#randNum#{";
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
        from = from.replace("@everyone", "**[REDACTED]**");
        from = from.replace("@here", "**[REDACTED]**");
        return from;
    }

    public static String testForShit(String from) {
        return from.replace("#shitpost#", "");
    }

    public static String testForLock(String from, IUser author, IGuild guild) {
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, author, guild)) {
            return from.replace("#lock#", "");
        }
        return from;
    }

    @TagAnnotation(name = "Channel", description = "Replaces tag with channel mention of the same ID", usage = "#channel#{[ChannelID]}", type = Constants.TAG_TYPE_INFO)
    public static String tagChannel(String from) {
        String prefix = "#channel#{";
        String suffix = "}";
        String contents;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                contents = StringUtils.substringBetween(from, prefix, suffix);
                if (contents != null) {
                    IChannel channel = Globals.getClient().getChannelByID(contents);
                    if (channel != null) {
                        from = from.replace(prefix + contents + suffix, channel.mention());
                    } else {
                        from = from.replaceFirst(Pattern.quote(contents), "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
        }
        return from;
    }

    @TagAnnotation(name = "DisplayName", description = "Replaces tag with DisplayName of the user with the same ID", usage = "#displayName#{[UserID]}", type = Constants.TAG_TYPE_INFO)
    public static String tagDisplayName(String from, IGuild guild) {
        String prefix = "#displayName#{";
        String suffix = "}";
        String contents;
        String lastAttempt;
        if (from.contains(prefix)) {
            do {
                lastAttempt = from;
                contents = StringUtils.substringBetween(from, prefix, suffix);
                if (contents != null) {
                    IUser user = Globals.getClient().getUserByID(contents);
                    if (user != null) {
                        from = from.replace(prefix + contents + suffix, user.getDisplayName(guild));
                    } else {
                        from = from.replaceFirst(Pattern.quote(contents), "#ERROR#");
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
}
