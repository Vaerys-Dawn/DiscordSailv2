package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.tags.TagList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TagObject {

    public final String name;
    public final String prefix;
    public final String suffix;
    public final String splitter;
    public final String desc;
    public final String usage;
    public final String error;
    public final int requiredArgs;
    private final int priority;
    List<TagType> types = new ArrayList<>();


    public TagObject(int priority, TagType... types) {
        this.priority = priority;
        requiredArgs = argsRequired();
        name = tagName();
        prefix = prefix();
        suffix = suffix();
        splitter = splitter();
        desc = desc();
        usage = usage();
        error = "#ERROR#:" + tagName();
        this.types = Arrays.asList(types);
    }

    public abstract String execute(String from, CommandObject command, String args);

    protected abstract String tagName();

    protected abstract int argsRequired();

    protected abstract String usage();

    protected abstract String desc();

    protected boolean isPassive() {
        return false;
    }

    public int getPriority() {
        return priority;
    }

    public boolean cont(String from) {
        if (requiredArgs != 0) {
            String tester = StringUtils.substringBetween(from, prefix, suffix);
            return tester != null && !tester.isEmpty();
        } else {
            return from.contains(prefix);
        }
    }

    public String contents(String from) {
        if (requiredArgs != 0) {
            String subString = StringUtils.substringBetween(from, prefix, suffix);
            if (subString != null) {
                return subString;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public List<String> getSplitContents(String from) {
        String contents = contents(from);
        if (contents != null) {
            return new ArrayList<>(Arrays.asList(contents.split(splitter)));
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getSplit(String args) {
        String toSplit = contents(args);
        String[] isSplit = toSplit.split(splitter);
        if (isSplit.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(toSplit.split(splitter)));
    }

    public String removeFirst(String from, String args) {
        return StringUtils.replaceOnce(from, args, "");
    }

    public String removeAll(String from, String args) {
        return from.replace(args, "");
    }

    public String replaceFirst(String from, String replace, String withThis) {
        return StringUtils.replaceOnce(from, replace, withThis);
    }

    public String replaceFirstTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return StringUtils.replaceOnce(from, prefix, withThis);
        } else {
            return StringUtils.replaceOnce(from, prefix + contents(from) + suffix, withThis);
        }
    }

    public String removeFirstTag(String from) {
        if (requiredArgs == 0) {
            return StringUtils.replaceOnce(from, prefix, "");
        } else {
            String toRemove = prefix + contents(from) + suffix;
            return StringUtils.replaceOnce(from, toRemove, "");
        }
    }

    public String replaceAllTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return from.replace(prefix, withThis);
        } else {
            return from.replace(prefix + contents(from) + suffix, withThis);
        }
    }

    public String removeAllTag(String from) {
        if (requiredArgs == 0) {
            return from.replace(prefix, "");
        } else {
            return from.replace(prefix + contents(from) + suffix, "");
        }
    }

    public String splitter() {
        return ";;";
    }

    protected String prefix() {
        if (requiredArgs == 0) {
            return name;
        } else {
            return name + "{";
        }
    }

    protected String suffix() {
        if (requiredArgs == 0) {
            return "";
        } else {
            return "}";
        }
    }

    public String handleTag(String from, CommandObject command, String args) {
        if (from == null) from = "";
        while (cont(from)) {
            int absoluteArgs = Math.abs(requiredArgs);
            if (requiredArgs == 0) {
                from = execute(from, command, args);
            } else if (requiredArgs < 0 && getSplit(from).size() >= absoluteArgs) {
                from = execute(from, command, args);
            } else if (requiredArgs == getSplit(from).size()) {
                from = execute(from, command, args);
            } else {
                from = replaceFirstTag(from, error);
            }
            if (from == null) from = "";
        }
        return from;
    }

    public XEmbedBuilder getInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle(name);
        StringBuilder descContents = new StringBuilder();
        descContents.append(desc);


        if (isPassive()) {
            descContents.append("\n\n**This tag cannot be used in commands and is passively run on every command.**");
        } else if (requiredArgs != 0) {
            descContents.append("\n**Usage:** " + "`" + prefix + usage + suffix + "`");
        } else {
            descContents.append("\n**Usage:** `" + name + "`");
        }
        descContents.append("\n\n**Types:** " + Utility.listEnumFormatter(types, true));
        builder.withDesc(descContents.toString());
        return builder;
    }

    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append(Utility.formatError(this));
        if (name == null || name.isEmpty()) {
            response.append("   > Tag name is empty.\n");
            isErrored = true;
        }
        if (desc == null || desc.isEmpty()) {
            response.append("   > Tag description is empty.\n");
            isErrored = true;
        }
        if (requiredArgs != 0 && (usage == null || usage.isEmpty())) {
            response.append("   > Tag usage is empty when args is required.\n");
            isErrored = true;
        }
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }

    public List<TagType> getTypes() {
        return types;
    }

    public static TagObject get(Class obj) {
        if (!TagObject.class.isAssignableFrom(obj)) {
            throw new IllegalArgumentException("Cannot Get Tag from Class (" + obj.getName() + ")");
        }
        for (TagObject t : TagList.get()) {
            if (t.getClass().getName().equals(obj.getName())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Could not find Tag (" + obj.getName() + ")");
    }
}
