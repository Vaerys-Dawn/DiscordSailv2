package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IRole;

import java.util.List;

public class TagIfRoleReplace extends TagReplaceObject {

    public TagIfRoleReplace(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitString = getSplit(from);
        from = removeFirstTag(from);
        boolean found = false;
        for (IRole r : command.user.roles) {
            if (StringUtils.containsIgnoreCase(r.getName(), splitString.get(0))) {
                found = true;
            }
        }
        if (found) {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(2)));
        } else {
            toReplace.add(new ReplaceObject(splitString.get(1), splitString.get(3)));
        }
        return from;
    }

    @Override
    protected String tagName() {
        return "<ifRoleReplace>";
    }

    @Override
    protected int argsRequired() {
        return 4;
    }

    @Override
    protected String usage() {
        return "RoleName" + splitter + "Replace" + splitter + "True" + splitter + "False";
    }

    @Override
    protected String desc() {
        return "Replaces the second argument with true if user running the command has a Role that contains argument and false if they do not.";
    }
}
