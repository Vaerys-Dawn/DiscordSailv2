package com.github.vaerys.handlers;

import com.github.vaerys.objects.LogObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class StringHandler {
    private StringBuffer string;

    public StringHandler(String s) {
        if (s == null) this.string = new StringBuffer("");
        else this.string = new StringBuffer(s);
    }

    public StringHandler(StringBuffer string) {
        this.string = string;
    }

    public StringHandler() {
        this.string = new StringBuffer();
    }

    public StringHandler setContent(String content) {
        string.replace(0, string.length(), content);
        return null;
    }

    public StringHandler emptyContent() {
        string.replace(0, string.length(), "");
        return this;
    }

    public StringHandler replace(String replaceFrom, String replaceTo) {
        setContent(string.toString().replace(replaceFrom, replaceTo));
        return this;
    }

    public StringHandler replace(StringBuffer toReplace, StringBuffer replaceWith) {
        return replace(toReplace.toString(), replaceWith.toString());
    }

    public StringHandler replace(StringHandler toReplace, StringHandler replaceWith) {
        return replace(toReplace.toString(), replaceWith.toString());
    }

    public StringHandler replace(int start, int end, String replacement) {
        string.replace(start, end, replacement);
        return this;
    }

    public StringHandler replaceRegex(String regex, String replacement) {
        setContent(string.toString().replaceAll(regex, replacement));
        return this;
    }

    public StringHandler replaceRegex(StringBuffer regex, StringBuffer replacement) {
        return replaceRegex(regex.toString(), replacement.toString());
    }

    public StringHandler replaceRegex(StringHandler regex, StringHandler replacement) {
        return replaceRegex(regex.toString(), replacement.toString());
    }

    public StringBuffer get() {
        return string;
    }

    @Override
    public String toString() {
        return string.toString();
    }

    public StringHandler append(String s) {
        string.append(s);
        return this;
    }

    public StringHandler append(float f) {
        string.append(f);
        return this;
    }

    public StringHandler append(double d) {
        string.append(d);
        return this;
    }

    public StringHandler append(char[] str) {
        string.append(str);
        return this;
    }

    public StringHandler append(CharSequence s) {
        string.append(s);
        return this;
    }

    public StringHandler append(StringBuffer s) {
        string.append(s);
        return this;
    }

    public StringHandler append(boolean b) {
        string.append(b);
        return this;
    }

    public StringHandler append(int i) {
        string.append(i);
        return this;
    }

    public StringHandler append(char c) {
        string.append(c);
        return this;
    }

    public StringHandler append(long l) {
        string.append(l);
        return this;
    }

    public StringHandler append(Object o) {
        string.append(o);
        return this;
    }

    public StringHandler appendFormatted(String s, Object... values) {
        string.append(String.format(s, values));
        return this;
    }

    public String[] split(String s) {
        return string.toString().split(s);
    }

    public void replaceOnce(String replace, String withThis) {
        setContent(StringUtils.replaceOnce(string.toString(), replace, withThis));
    }

    public boolean startsWith(String s) {
        return s.startsWith(s);
    }

    public void addViaJoin(List<LogObject> allLogs, String s) {
        append(String.join(s, allLogs.stream().map(logObject -> logObject.getOutput()).collect(Collectors.toList())));
    }

    public boolean isEmpty() {
        return string.toString().isEmpty();
    }

    public void appendFront(String s) {
        setContent(s + string.toString());
    }

    public void format(Object... values) {
        setContent(String.format(string.toString(), values));
    }

    public int length() {
        return string.length();
    }

    public String substring(int start, int end) {
        return string.substring(start, end);
    }
}
