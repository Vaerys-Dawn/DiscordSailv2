package Main;

import Annotations.TagAnnotation;
import Objects.ReplaceObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 22/10/2016.
 */
public class TagSystem {

    public static String tagSystem(String contents, IMessage message, String args) {
        String response = contents;
        response = tagArgs(response, args);
        response = tagRandom(response);
        response = tagIfRole(response, message.getAuthor(), message.getGuild());
        response = tagIfName(response, message.getAuthor(), message.getGuild());
        response = tagRegex(response,"#replace#{","}",";;");
        response = tagRegex(response,"#!replace#(",")#!r#","::");
        response = tagSpacer(response);
        response = tagNoNL(response);
        response = response.replace("#author#", message.getAuthor().getDisplayName(message.getGuild()));
        response = response.replace("#channel#", message.getChannel().mention());
        response = response.replace("#guild#", message.getGuild().getName());
        response = response.replace("#authorID#", message.getAuthor().getID());
        response = response.replace("#channelID#", message.getChannel().getID());
        response = response.replace("#guildID#", message.getGuild().getID());
        response = tagMentionRemover(response);
        return response;
    }

    public static String getTagList(String type) {
        Method[] methods = TagSystem.class.getMethods();
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 1; i < 11;i++) {
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

    @TagAnnotation(name = "Args", description = "This tag replaces the tag with anything after the custom command.", usage = "#args#", priority = 1)
    public static String tagArgs(String from, String args) {
        return from.replace("#args#", args);
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
    public static String tagRegex(String from,String prefix, String suffix, String splitter) {
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
                        toReplace.add(new ReplaceObject(splitRegex.get(0),splitRegex.get(1)));
                    } else {
                        from = from.replace(tagRegex, "#ERROR#");
                    }
                }
            } while (StringUtils.countMatches(from, prefix) > 0 && (!lastAttempt.equals(from)));
            for (ReplaceObject r: toReplace){
                from = from.replace(r.getFrom(),r.getTo());
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

    @TagAnnotation(name = "NoBreak", description = "Removes a \\n from the command if you put this tag at the end of the line.",usage = "#!break#")
    public static String tagNoNL(String from) {
        return from.replace("#!break#\n", "");
    }

    public static String tagMentionRemover(String from) {
        from = from.replace("@everyone", "**[REDACTED]**");
        from = from.replace("@here", "**[REDACTED]**");
        return from;
    }

    public static String testForShit(String from) {
        return from.replace("#isShitpost#","");
    }

    public static String testForLock(String from, IUser author, IGuild guild) {
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES},author,guild)){
            return from.replace("#lock#","");
        }
        return from;
    }
}
