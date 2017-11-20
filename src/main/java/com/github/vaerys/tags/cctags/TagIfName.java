package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagIfName extends TagObject {

    public TagIfName(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitString = getSplit(from);
        String displayName = command.user.displayName;
        String userName = command.user.name;

        if (StringUtils.containsIgnoreCase(displayName, splitString.get(0)) ||
                StringUtils.containsIgnoreCase(userName, splitString.get(0))) {
            from = replaceFirstTag(from, splitString.get(1));
        } else {
            from = replaceFirstTag(from, splitString.get(2));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifName>";
    }

    @Override
    public int argsRequired() {
        return 3;
    }

    @Override
    public String usage() {
        return "Name" + splitter + "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the tag with true if user running the command has the first argument in their name or nickname and false if they do not.";
    }
}
