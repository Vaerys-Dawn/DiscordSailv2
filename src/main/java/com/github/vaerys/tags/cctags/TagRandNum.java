package com.github.vaerys.tags.cctags;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.TagObject;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TagRandNum extends TagObject {

    public TagRandNum(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        List<String> spitArgs = getSplit(from);
        try {
            long minNum = Long.parseLong(spitArgs.get(0));
            long maxNum = Long.parseLong(spitArgs.get(1));
            from = replaceFirstTag(from, ThreadLocalRandom.current().nextLong(minNum, maxNum + 1) + "");
        } catch (NumberFormatException e) {
            from = replaceFirstTag(from, "#ERROR#:" + name);
        } catch (IllegalArgumentException e) {
            from = replaceFirstTag(from, "#ERROR#:" + name);
        }
        return from;
    }

    @Override
    public String tagName() {
        return "<randNum>";
    }

    @Override
    public int argsRequired() {
        return 2;
    }

    @Override
    public String usage() {
        return "MinNum" + splitter + "MaxNum";
    }

    @Override
    public String desc() {
        return "replaces the tag with a number between the first argument and last argument inclusive.";
    }
}
