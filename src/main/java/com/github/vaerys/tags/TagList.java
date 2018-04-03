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

    private static final List<TagObject> tags = new ArrayList<TagObject>(){{
        //args
        add(new TagSearchTags(0, TagType.CC));
        add(new TagIfArgsEmpty(1, TagType.CC));
        add(new TagIfArgsEmptyReplace(2, TagType.CC));
        add(new TagArgs(3, TagType.CC));
        //simple string replaces
        add(new TagNoBreak(10, TagType.CC, TagType.INFO, TagType.DAILY, TagType.JOIN_MESSAGES));
        add(new TagSpacer(10, TagType.CC, TagType.INFO, TagType.DAILY, TagType.JOIN_MESSAGES));
        add(new TagAuthor(10, TagType.CC));
        add(new TagUsername(10, TagType.CC));
        add(new TagEmoji(10, TagType.INFO));
        add(new TagLevel(10, TagType.LEVEL, TagType.PIXEL));
        add(new TagUser(10, TagType.LEVEL, TagType.JOIN_MESSAGES));
        add(new TagChannel(10, TagType.INFO));
        add(new TagDisplayName(10, TagType.INFO));
        add(new TagServer(10, TagType.CC, TagType.JOIN_MESSAGES));
        add(new TagChannelMention(10, TagType.CC));
        //single args
        add(new TagSingleArgs(20, TagType.CC));
        //random tags (part 1)
        add(new TagRandom(30, TagType.CC, TagType.DAILY, TagType.JOIN_MESSAGES));
        add(new TagReplaceRandom(31, TagType.CC, TagType.JOIN_MESSAGES));
        //if tags
        add(new TagIfRole(40, TagType.CC));
        add(new TagIfName(41, TagType.CC));
        add(new TagIfArgs(42, TagType.CC));
        add(new TagIfChannel(43, TagType.CC));
        //replace tags
        add(new TagIfRoleReplace(50, TagType.CC));
        add(new TagIfNameReplace(51, TagType.CC));
        add(new TagIfArgsReplace(52, TagType.CC));
        add(new TagIfChannelReplace(53, TagType.CC));
        add(new TagReplace(54, TagType.CC));
        add(new TagReplaceSpecial(55, TagType.CC));
        add(new TagRegex(56, TagType.CC));
        //random tags (part 2)
        add(new TagRandNum(60, TagType.CC, TagType.DAILY, TagType.JOIN_MESSAGES));
        add(new TagRandEmote(61, TagType.CC, TagType.DAILY, TagType.LEVEL, TagType.JOIN_MESSAGES));
        add(new TagReplaceError(62, TagType.CC));
        //empty tag
        add(new TagEmpty(70, TagType.CC, TagType.DAILY, TagType.JOIN_MESSAGES));
        //no string additions should be allowed past this point;
        add(new TagAllCaps(80, TagType.CC));
        add(new TagRemoveSanitizeTag(81, TagType.CC));
        add(new TagRemovePrep(81, TagType.CC));
        add(new TagCheckLength(82, TagType.CC));
        add(new TagDelCall(85, TagType.CC));
        add(new TagRemoveMentions(100, TagType.CC, TagType.DAILY, TagType.INFO));
        add(new TagEmbedImage(999, TagType.CC));
    }};

    public static List<TagObject> get(boolean validate) {
        if (validate) validateTags(tags);
        return tags;
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

    private static void validateTags(List<TagObject> tags) {
        for (TagObject c : tags) {
            logger.trace("Validating Tag: " + c.getClass().getName());
            String errorReport = c.validate();
            Globals.addToErrorStack(errorReport);
        }
        sort(tags);
    }

    public static List<String> getNames(TagType type) {
        return getType(type).stream().map(tagObject -> tagObject.name).collect(Collectors.toList());
    }

    public static void sort(List<TagObject> list) {
        list.sort(Comparator.comparingInt(TagObject::getPriority));
    }

    public static TagObject getTag(Class obj) {
        if (!TagObject.class.isAssignableFrom(obj)) {
            throw new IllegalArgumentException("Cannot Get Tag from Class (" + obj.getName() + ")");
        }
        for (TagObject t : get()) {
            if (t.getClass().getName().equals(obj.getName())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Could not find Tag (" + obj.getName() + ")");
    }
}
