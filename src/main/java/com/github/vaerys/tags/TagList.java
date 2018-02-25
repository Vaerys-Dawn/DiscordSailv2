package com.github.vaerys.tags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.tags.cctags.*;
import com.github.vaerys.tags.infotags.TagChannel;
import com.github.vaerys.tags.infotags.TagDisplayName;
import com.github.vaerys.tags.infotags.TagEmoji;
import com.github.vaerys.tags.leveluptags.TagLevel;
import com.github.vaerys.tags.leveluptags.TagUser;
import com.github.vaerys.templates.TagObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TagList {

    final static Logger logger = LoggerFactory.getLogger(TagList.class);

    private static List<TagObject> tags = new ArrayList<>();


    public static void init() {
        //args
        tags.add(new TagSearchTags(0, TagType.CC));
        tags.add(new TagIfArgsEmpty(1, TagType.CC));
        tags.add(new TagIfArgsEmptyReplace(2, TagType.CC));
        tags.add(new TagArgs(3, TagType.CC));
        //simple string replaces
        tags.add(new TagNoBreak(10, TagType.CC, TagType.INFO, TagType.DAILY));
        tags.add(new TagSpacer(10, TagType.CC, TagType.INFO, TagType.DAILY));
        tags.add(new TagAuthor(10, TagType.CC));
        tags.add(new TagUsername(10, TagType.CC));
        tags.add(new TagEmoji(10, TagType.INFO));
        tags.add(new TagLevel(10, TagType.LEVEL, TagType.PIXEL));
        tags.add(new TagUser(10, TagType.LEVEL));
        tags.add(new TagChannel(10, TagType.INFO));
        tags.add(new TagDisplayName(10, TagType.INFO));
        tags.add(new TagServer(10, TagType.CC));
        tags.add(new TagChannelMention(10, TagType.CC));
        //single args
        tags.add(new TagSingleArgs(20, TagType.CC));
        //random tags (part 1)
        tags.add(new TagRandom(30, TagType.CC, TagType.DAILY));
        tags.add(new TagReplaceRandom(31, TagType.CC));
        //if tags
        tags.add(new TagIfRole(40, TagType.CC));
        tags.add(new TagIfName(41, TagType.CC));
        tags.add(new TagIfArgs(42, TagType.CC));
        tags.add(new TagIfChannel(43, TagType.CC));
        //replace tags
        tags.add(new TagReplace(50, TagType.CC));
        tags.add(new TagReplaceSpecial(51, TagType.CC));
        tags.add(new TagRegex(52, TagType.CC));
        //random tags (part 2)
        tags.add(new TagRandNum(60, TagType.CC, TagType.DAILY));
        tags.add(new TagRandEmote(61, TagType.CC, TagType.DAILY, TagType.LEVEL));
        tags.add(new TagReplaceError(62, TagType.CC));
        //empty tag
        tags.add(new TagEmpty(70, TagType.CC, TagType.DAILY));
        //no string additions should be allowed past this point;
        tags.add(new TagAllCaps(80, TagType.CC));
        tags.add(new TagRemoveSanitizeTag(81, TagType.CC));
        tags.add(new TagRemovePrep(81, TagType.CC));
        tags.add(new TagCheckLength(82, TagType.CC));
        tags.add(new TagDelCall(85, TagType.CC));
        tags.add(new TagRemoveMentions(100, TagType.CC, TagType.DAILY, TagType.INFO));
        tags.add(new TagEmbedImage(999, TagType.CC));

        validate(tags);
    }

    public static List<TagObject> get() {
        return tags;
    }

    public static List<TagObject> getType(TagType type) {
        List<TagObject> list = new LinkedList<>();
        for (TagObject o : tags) {
            for (TagType t : o.getTypes()) {
                if (t == type) {
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
            Globals.addToErrorStack(errorReport);
        }
        sort(tags);
    }

    public static List<String> getNames(String type) {
        return getNames(TagType.get(type));
    }

    public static List<String> getNames(TagType type) {
        return getType(type).stream().map(tagObject -> tagObject.name).collect(Collectors.toList());
    }

    public static void sort(List<TagObject> list) {
        list.sort(Comparator.comparingInt(TagObject::getPriority));
    }
}
