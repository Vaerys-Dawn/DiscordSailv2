package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.ReplaceObject;

import java.util.ArrayList;
import java.util.List;

public abstract class TagReplaceObject extends TagObject {

    public List<ReplaceObject> toReplace = new ArrayList<>();

    public TagReplaceObject(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String handleTag(String from, CommandObject command, String args) {
        from = super.handleTag(from, command, args);
        for (ReplaceObject r : toReplace) {
            from = from.replace(r.getFrom(), r.getTo());
        }
        toReplace = new ArrayList<>();
        return from;
    }
}
