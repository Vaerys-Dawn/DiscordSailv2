package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagIfRole extends TagObject {

    public TagIfRole(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitString = getSplit(from);
        boolean found = false;
        for (Role r : command.user.roles) {
            if (StringUtils.containsIgnoreCase(r.getName(), splitString.get(0))) {
                found = true;
            }
        }
        if (found) {
            from = replaceFirstTag(from, splitString.get(1));
        } else {
            from = replaceFirstTag(from, splitString.get(2));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifRole>";
    }

    @Override
    public int argsRequired() {
        return 3;
    }

    @Override
    public String usage() {
        return "Role" + splitter + "True" + splitter + "False";
    }

    @Override
    public String desc() {
        return "Replaces the tag with true if user running the command has a Role that contains argument and false if they do not.";
    }
}
