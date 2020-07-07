package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagAdminSubTagObject extends TagObject {

    protected String usageName;

    protected final String ERROR_PIXELS_DISABLED = "#ERROR#:<" + tagName() + ":PIXELS_DISABLED" + Command.spacer + ">";

    public String getSubPrefix(String from) {
        return "<" + tagName() + ":" + getSubTag(from) + ">{";
    }

    private String getFullTag(String from) {
        return getSubPrefix(from) + getContents(from) + suffix;
    }

    @Override
    public String execute(String from, CommandObject command, String args) {
        AdminCCObject temp = new AdminCCObject("<Temp>", command.client.bot.longID, "");
        return execute(from, command, args, temp);
    }

    public abstract String execute(String from, CommandObject command, String args, AdminCCObject cc);

    public TagAdminSubTagObject(int priority, TagType... types) {
        super(priority, types);
        name = "<" + tagName() + ">";
        usageName = "<" + tagName() + ":" + subTagUsage() + ">" + (requiredArgs != 0 ? "{" : "");
        error = "#ERROR#:" + "<" + tagName() + Command.spacer + ">";
    }

    @Override
    protected String prefix() {
        prefix = "<" + tagName() + ":([\\w| ]+?)>";
        if (requiredArgs != 0) {
            prefix += "\\{";
        }
        return prefix;
    }

    public String getSubTag(String from) {
        if (requiredArgs != 0) {
            try {
                String tagPrefix = "<" + tagName() + ":";
                String subTag = StringUtils.substringBetween(from, tagPrefix, ">");
                return Pattern.compile("([\\w| ]+?)").matcher(subTag).matches() ? subTag : "";
            } catch (Exception e) {
                return "";
            }
        } else {
            try {
                Matcher p = Pattern.compile(prefix).matcher(from);
                p.find();
                return p.group(1);
            } catch (Exception e) {
                return "";
            }
        }
    }

    public String getContents(String from) {
        if (requiredArgs != 0) {
            try {
                return StringUtils.substringBetween(from, getSubPrefix(from), suffix);
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getUsage() {
        return requiredArgs != 0 ? usageName + usage + suffix : usageName;
    }

    public XEmbedBuilder getInfo(CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle(name);
        StringBuilder descContents = new StringBuilder();
        descContents.append(desc);

        if (isPassive()) {
            descContents.append("\n\n**This tag cannot be used in commands and is passively run on every command.**");
        } else if (requiredArgs != 0) {
            descContents.append("\n**Usage:** " + "`" + usageName + usage + suffix + "`");
        } else {
            descContents.append("\n**Usage:** `" + usageName + "`");
        }
        descContents.append("\n\n**Types:** " + Utility.listEnumFormatter(types, true));
        builder.setDescription(descContents.toString());
        return builder;
    }

    @Override
    public boolean cont(String from) {
        if (requiredArgs != 0) {
            try {
                return StringUtils.substringBetween(from, getSubPrefix(from), suffix) != null;
            } catch (Exception e) {
                return false;
            }
        } else {
            return Pattern.compile(prefix).matcher(from).find();
        }
    }

    protected abstract String subTagUsage();

    public String removeFirst(String from, String args) {
        return StringUtils.replaceOnce(from, args, "");
    }

    public String replaceFirstTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return from.replaceFirst(prefix, withThis);
        } else {
            return StringUtils.replaceOnce(from, getFullTag(from), withThis);
        }
    }

    public String removeFirstTag(String from) {
        if (requiredArgs == 0) {
            return from.replaceFirst(prefix, "");
        } else {
            return StringUtils.replaceOnce(from, getFullTag(from), "");
        }
    }

    public String replaceAllTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return from.replaceAll(prefix, withThis);
        } else {
            return from.replaceAll(prefix + group + suffix, prepReplace(withThis));
        }
    }

    public String removeAllTag(String from) {
        if (requiredArgs == 0) {
            return from.replaceAll(prefix, "");
        } else {
            return from.replaceAll(prefix + group + suffix, "");
        }
    }

}
