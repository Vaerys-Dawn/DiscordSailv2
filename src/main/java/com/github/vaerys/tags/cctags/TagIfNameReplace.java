package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagIfNameReplace extends TagReplaceObject {

    public TagIfNameReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitString = getSplit(from);
        from = removeFirstTag(from);
        String displayName = command.user.displayName;
        String userName = command.user.name;

        if (StringUtils.containsIgnoreCase(displayName, splitString.get(0)) ||
                StringUtils.containsIgnoreCase(userName, splitString.get(0))) {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(2)));
        } else {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(3)));
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<ifNameReplace>";
    }

    @Override
    protected int argsRequired() {
        return 4;
    }

    @Override
    protected String usage() {
        return "Name" + splitter + "Replace" + splitter + "True" + splitter + "False";
    }

    @Override
    protected String desc() {
        return "Replaces the second argument with true if globalUser running the command has the first argument in their name or nickname and false if they do not.";
    }
}
