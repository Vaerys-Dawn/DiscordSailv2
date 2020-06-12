package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.TagObject;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TagSeededRandom extends TagObject {

    public TagSeededRandom(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> splitArgs = getSplit(from);
        if (splitArgs.size() < 2) return replaceFirstTag(from, error);

        long seed = Objects.hash(tagName(), splitArgs.get(0));      // use class name AND Seed input for rng seed
        Random random = new Random(seed);
        
        return replaceFirstTag(from, splitArgs.get(random.nextInt(splitArgs.size()-1)+1));  // exclude seed
    }

    @Override
    public String tagName() {
        return "<seededRandom>";
    }

    @Override
    public int argsRequired() {
        return -1;
    }

    @Override
    public String usage() {
        return "Seed" + splitter + "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    public String desc() {
        return "Replaces the tag with a random value. A seed will make the result remain the same between runs, dependent on the seed value.";
    }
}
