package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TagObject {

    public String name;
    public String prefix;
    public String suffix;
    public final String splitter;
    public final String desc;
    public final String usage;
    public String error;
    public final int requiredArgs;
    private final int priority;
    List<TagType> types;
    public String group = "((.|\n)+?)";

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

    public abstract String tagName();

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

    public String getContents(String from) {
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

    public List<String> getSplit(String from) {
        String toSplit = getContents(from);
        String[] isSplit = toSplit.split(splitter);
        if (isSplit.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(isSplit));
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
            return StringUtils.replaceOnce(from, prefix + getContents(from) + suffix, withThis);
        }
    }

    public String removeFirstTag(String from) {
        if (requiredArgs == 0) {
            return StringUtils.replaceOnce(from, prefix, "");
        } else {
            String toRemove = prefix + getContents(from) + suffix;
            return StringUtils.replaceOnce(from, toRemove, "");
        }
    }

    public String replaceAllTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return from.replace(prefix, withThis);
        } else {
            return from.replace(prefix + getContents(from) + suffix, withThis);
        }
    }

    public String removeAllTag(String from) {
        if (requiredArgs == 0) {
            return from.replace(prefix, "");
        } else {
            return from.replace(prefix + getContents(from) + suffix, "");
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

    public String getUsage() {
        return requiredArgs != 0 ? prefix + usage + suffix : prefix;
    }

    public String handleTag(String from, CommandObject command, String args) {
        if (from == null) from = "";
        while (cont(from)) {
            int absoluteArgs = Math.abs(requiredArgs);
            if (requiredArgs == 0 ||
                    requiredArgs < 0 && getSplit(from).size() >= absoluteArgs ||
                    requiredArgs == getSplit(from).size()) {
                from = execute(from, command, args);
            } else {
                from = replaceFirstTag(from, error);
            }
            if (from == null) from = "";
        }
        return from;
    }

    public String handleTag(String from, CommandObject command, String args, AdminCCObject cc) {
        if (from == null) from = "";
        if (isPassive()) {
            return handleTag(from, command, args);
        }
        String last = "";
        while (cont(from) && !last.equals(from)) {
            last = from;
            int absoluteArgs = Math.abs(requiredArgs);
            if (requiredArgs == 0 ||
                    requiredArgs < 0 && getSplit(from).size() >= absoluteArgs ||
                    requiredArgs == getSplit(from).size()) {
                if (this instanceof TagAdminSubTagObject) {
                    TagAdminSubTagObject specialObject = (TagAdminSubTagObject) this;
                    from = specialObject.execute(from, command, args, cc);
                } else {
                    from = execute(from, command, args);
                }
            } else {
                from = replaceFirstTag(from, error);
            }
            if (from == null) from = "";
        }
        return from;
    }

    public XEmbedBuilder getInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(name);
        StringBuilder descContents = new StringBuilder();
        descContents.append(desc);


        if (isPassive()) {
            descContents.append("\n\n**This tag cannot be used in commands and is passively run on every command.**");
        } else if (requiredArgs != 0) {
            descContents.append("\n**Usage:** " + "`" + prefix + usage + suffix + "`");
        } else {
            descContents.append("\n**Usage:** `" + prefix + "`");
        }
        descContents.append("\n\n**Types:** " + Utility.listEnumFormatter(types, true));
        builder.setDescription(descContents.toString());
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
        return TagList.getTag(obj);
    }

    public String prepReplace(String withThis) {
        return withThis.replace("$", "\\$");
    }

    public String getTypeString() {
        return String.join(", ", getTypes().stream().map(ty -> ty.toString()).collect(Collectors.toList()));
    }
}
