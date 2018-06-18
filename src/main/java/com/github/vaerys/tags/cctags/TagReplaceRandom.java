package com.github.vaerys.tags.cctags;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;

import java.util.List;
import java.util.Random;

public class TagReplaceRandom extends TagReplaceObject {

    public TagReplaceRandom(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitArgs = getSplit(from);
        Random random = new Random();
        int randomNum = random.nextInt(splitArgs.size() - 1);
        from = removeFirstTag(from);
        toReplace.add(new ReplaceObject(splitArgs.get(0), splitArgs.get(randomNum + 1)));
        return from;
    }

    @Override
    public String tagName() {
        return "<replaceRandom>";
    }

    @Override
    public int argsRequired() {
        return -2;
    }

    @Override
    public String usage() {
        return "Replace" + splitter + "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    public String desc() {
        return "Replaces the first argument with a random value.";
    }
}
