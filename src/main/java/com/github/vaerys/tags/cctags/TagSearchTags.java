package com.github.vaerys.tags.cctags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;

public class TagSearchTags extends TagObject {

    public TagSearchTags(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        return removeAllTag(from);
    }

    public void addTag(CCommandObject object, String args) {
        String from = object.getContents(false);
        List<String> splitContents = new ArrayList<>(Arrays.asList(args.split("(, |,|\n|" + splitter + ")")));
        if (cont(from)) {
            splitContents.addAll(getSpliContents(from));
        }
        StringBuilder newContents = new StringBuilder();
        splitContents.forEach(s -> newContents.append(s + splitter));
        newContents.delete(newContents.length() - splitter.length(), newContents.length());
        if (cont(from)) {
            from = replaceAllTag(from, prefix + newContents + suffix);
        } else {
            from += prefix + newContents + suffix;
        }
        object.setContents(from);
    }

    @Override
    public String tagName() {
        return "<searchTags>";
    }

    @Override
    public int argsRequired() {
        return -1;
    }

    @Override
    public String usage() {
        return "tag1;;tag2;;tag...";
    }

    @Override
    public String desc() {
        return "This tag removes itself great for adding search tags.";
    }
}
