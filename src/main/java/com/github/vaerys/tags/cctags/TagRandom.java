package com.github.vaerys.tags.cctags;

import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.templates.TagObject;

import java.util.List;
import java.util.Random;

public class TagRandom extends TagObject {

    public TagRandom(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        Random random = Globals.getGlobalRandom();
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
