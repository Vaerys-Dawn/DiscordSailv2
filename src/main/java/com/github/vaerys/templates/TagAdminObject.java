package com.github.vaerys.templates;

import com.github.vaerys.enums.TagType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagAdminObject extends TagAdminSubTagObject{

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
                Matcher p = Pattern.compile(prefix + group + suffix).matcher(from);
                p.find();
                return p.group(0) != null;
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
                Matcher p = Pattern.compile(prefix + group + suffix).matcher(from);
                p.find();
                return p.group(1);
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

}
