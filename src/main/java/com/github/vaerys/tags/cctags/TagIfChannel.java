package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class TagIfChannel extends TagObject {

    public TagIfChannel(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitContents = getSplitContents(from);
        if (!Pattern.compile("<#[0-9]*>").matcher(splitContents.get(0)).matches()) {
            return replaceFirstTag(from, error);
        } else {
            long id;
            try {
                id = Long.parseUnsignedLong(StringUtils.substringBetween(splitContents.get(0), "<#", ">"));
            } catch (NumberFormatException e) {
                return replaceFirstTag(from, error);
            }
            if (id == command.channel.longID) {
                return replaceFirstTag(from, splitContents.get(1));
            } else {
                return replaceFirstTag(from, splitContents.get(2));
            }
        }
    }

    @Override
    public String tagName() {
        return "<ifChannel>";
    }

    @Override
    public int argsRequired() {
        return 3;
    }

    @Override
    public String usage() {
        return "#channel" + splitter + "true" + splitter + "false";
    }

    @Override
    public String desc() {
        return "replaces itself with text based of if its in the specified channel or not.";
    }
}
