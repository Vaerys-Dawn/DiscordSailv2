package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class TagIfChannelReplace extends TagReplaceObject {

    public TagIfChannelReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitString = getSplit(from);
        from = removeFirstTag(from);
        long id;
        if (!Pattern.compile("<#[0-9]*>").matcher(splitString.get(0)).matches()) {
            return replaceFirstTag(from, error);
        } else {
            try {
                id = Long.parseUnsignedLong(StringUtils.substringBetween(splitString.get(0), "<#", ">"));
            } catch (NumberFormatException e) {
                return replaceFirstTag(from, error);
            }
            if (id == command.channel.longID) {
                toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(2)));
            } else {
                toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(3)));
            }
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifChannelReplace>";
    }

    @Override
    protected int argsRequired() {
        return 4;
    }

    @Override
    protected String usage() {
        return "Channel" + splitter + "Replace" + splitter + "True" + splitter + "False";
    }

    @Override
    protected String desc() {
        return "replaces the second argument with text based of if its in the specified channel or not.";
    }
}
