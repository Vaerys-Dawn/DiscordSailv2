package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagAdminObject extends TagAdminSubTagObject {

    public TagAdminObject(int priority, TagType... types) {
        super(priority, types);
        prefix = "<" + tagName() + ">" + (requiredArgs == 0 ? "" : "\\{");
        usageName = "<" + tagName() + ">" + (requiredArgs == 0 ? "" : "{");
        suffix = "}";
    }

    @Override
    protected String subTagUsage() {
        return "";
    }

    @Override
    public boolean cont(String from) {
        if (requiredArgs != 0) {
            try {
                return StringUtils.substringBetween(from, usageName, suffix) != null;
            } catch (Exception e) {
                return false;
            }
        } else {
            return Pattern.compile(prefix).matcher(from).find();
        }
    }

    public String contents(String from) {
        if (requiredArgs != 0) {
            try {
                return StringUtils.substringBetween(from, usageName, suffix);
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String replaceFirstTag(String from, String withThis) {
        if (requiredArgs == 0) {
            return from.replaceFirst(prefix, withThis);
        } else {
            return StringUtils.replaceOnce(from, usageName + contents(from) + suffix, withThis);
        }
    }

    public String removeFirstTag(String from) {
        if (requiredArgs == 0) {
            return from.replaceFirst(prefix, "");
        } else {
            return StringUtils.replaceOnce(from, usageName + contents(from) + suffix, "");
        }
    }
}
