package com.github.vaerys.tags.cctags;

public class TagReplaceSpecial extends TagReplace {

    public TagReplaceSpecial(int priority, String... types) {
        super(priority, types);
    }

    @Override
    public String tagName() {
        return "<replace!>";
    }

    @Override
    public String desc() {
        return "Does the same as the replace tag but it is nestable within other tags.";
    }

    @Override
    public String splitter() {
        return "::";
    }

    @Override
    public String prefix() {
        return name + "(";
    }

    @Override
    public String suffix() {
        return ")</r>";
    }
}
