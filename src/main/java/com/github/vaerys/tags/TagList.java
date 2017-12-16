package com.github.vaerys.tags;

import com.github.vaerys.tags.cctags.*;
import com.github.vaerys.tags.infotags.TagChannel;
import com.github.vaerys.tags.infotags.TagDisplayName;
import com.github.vaerys.tags.infotags.TagEmoji;
import com.github.vaerys.tags.leveluptags.TagLevel;
import com.github.vaerys.tags.leveluptags.TagUser;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TagList {

    final static Logger logger = LoggerFactory.getLogger(TagList.class);

    private static List<TagObject> tags = new ArrayList<>();

    public final static String CC = "CustomCommand";
    public final static String INFO = "Info";
    public final static String DAILY = "Daily";
    public final static String LEVEL = "LevelUp";


    public static void init() {
        //args
        tags.add(new TagSearchTags(0, CC));
        tags.add(new TagIfArgsEmpty(1, CC));
        tags.add(new TagIfArgsEmptyReplace(2, CC));
        tags.add(new TagArgs(3, CC));
        //simple string replaces
        tags.add(new TagNoBreak(10, CC, INFO, DAILY));
        tags.add(new TagSpacer(10, CC, INFO, DAILY));
        tags.add(new TagAuthor(10, CC));
        tags.add(new TagUsername(10, CC));
        tags.add(new TagEmoji(10, INFO));
        tags.add(new TagLevel(10, LEVEL, Command.TYPE_PIXEL));
        tags.add(new TagUser(10, LEVEL));
        tags.add(new TagChannel(10, INFO));
        tags.add(new TagDisplayName(10, INFO));
        //single args
        tags.add(new TagSingleArgs(20, CC));
        //random tags (part 1)
        tags.add(new TagRandom(30, CC, DAILY));
        tags.add(new TagReplaceRandom(31, CC));
        //if tags
        tags.add(new TagIfRole(40, CC));
        tags.add(new TagIfName(41, CC));
        tags.add(new TagIfArgs(42, CC));
        //replace tags
        tags.add(new TagReplace(50, CC));
        tags.add(new TagReplaceSpecial(51, CC));
        tags.add(new TagRegex(52, CC));
        //random tags (part 2)
        tags.add(new TagRandNum(60, CC, DAILY));
        tags.add(new TagRandEmote(61, CC, DAILY, LEVEL));
        tags.add(new TagReplaceError(62, CC));
        //empty tag
        tags.add(new TagEmpty(70, CC, DAILY));
        //no string additions should be allowed past this point;
        tags.add(new TagAllCaps(80, CC));
        tags.add(new TagRemoveSanitizeTag(81, CC));
        tags.add(new TagRemovePrep(81, CC));
        tags.add(new TagCheckLength(82, CC));
        tags.add(new TagDelCall(85, CC));
        tags.add(new TagRemoveMentions(100, CC, DAILY, INFO));
        tags.add(new TagEmbedImage(999, CC));

        validate(tags);
    }

    public static List<TagObject> get() {
        return tags;
    }

    public static List<TagObject> getType(String type) {
        List<TagObject> list = new ArrayList<>();
        for (TagObject o : tags) {
            for (String t : o.getTypes()) {
                if (t.equalsIgnoreCase(type)) {
                    list.add(o);
                    break;
                }
            }
        }
        sort(list);
        return list;
    }

    private static void validate(List<TagObject> tags) {
        for (TagObject c : tags) {
            logger.trace("Validating Tag: " + c.getClass().getName());
            String errorReport = c.validate();
            if (errorReport != null) {
                logger.error(errorReport);
                System.exit(-1);
            }
        }
        sort(tags);
    }

    public static List<String> getNames(String type) {
        return getType(type).stream().map(tagObject -> tagObject.name).collect(Collectors.toList());
    }

    public static void sort(List<TagObject> list) {
        list.sort(Comparator.comparingInt(TagObject::getPriority));
    }
}
