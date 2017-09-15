package com.github.vaerys.interfaces;

import com.github.vaerys.commands.CommandObject;

public interface TagInterface {
    String execute(String from, CommandObject command, String args);

    int priority();

    String tagName();

    int argsRequired();

    String usage();

    String desc();

    default String splitter() {
        return ";;";
    }

    default String prefix() {
        if (argsRequired() == 0) {
            return tagName();
        } else {
            return tagName() + "{";
        }
    }

    default String suffix() {
        if (argsRequired() == 0) {
            return "";
        } else {
            return "}";
        }
    }

}
