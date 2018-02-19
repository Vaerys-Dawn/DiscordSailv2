package com.github.vaerys.tags.cctags;

import java.util.List;
import java.util.Random;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;

public class TagRandom extends TagObject {

    public TagRandom(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        Random random = new Random();
        List<String> splitArgs = getSplit(from);
        return replaceFirstTag(from, splitArgs.get(random.nextInt(splitArgs.size())));
    }

    @Override
    public String tagName() {
        return "<random>";
    }

    @Override
    public int argsRequired() {
        return -1;
    }

    @Override
    public String usage() {
        return "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    public String desc() {
        return "Replaces the tag with a random value.";
    }
}
