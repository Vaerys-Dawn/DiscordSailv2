package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.CCommandObject;
import com.github.vaerys.templates.TagObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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
            splitContents.addAll(getSplitContents(from));
        }
        String newContents = String.join(";;", splitContents);
        if (cont(from)) {
            from = replaceAllTag(from, prefix + newContents + suffix);
        } else {
            from += prefix + newContents + suffix;
        }
        object.setContents(from);
    }

    public boolean removeTag(CCommandObject object, String args) {
        String from = object.getContents(false);
        if (!cont(from)) {
            return false;
        }
        List<String> splitContents = getSplitContents(from);

        boolean removed = false;
        ListIterator iterator = splitContents.listIterator();
        while (iterator.hasNext()) {
            String tag = (String) iterator.next();
            if (tag.equalsIgnoreCase(args)) {
                iterator.remove();
                removed = true;
            }
        }
        String newContents = String.join(";;", splitContents);
        if (removed) {
            object.setContents(replaceAllTag(from, prefix + newContents + suffix));
        }
        return removed;
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
