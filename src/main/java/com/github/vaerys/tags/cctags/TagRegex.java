package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TagRegex extends TagReplaceObject {

    public static final long REGEX_TIMEOUT_MS = 4000;  // 4 seconds

    public TagRegex(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitArgs = getSplit(from);
        String test = "Quite a long testing string.";
        try {
            timedReplace(test, splitArgs.get(0), test, 1000);  // dummy replace
        } catch (TimedCharSequence.TimeoutException e) {
            from = replaceFirstTag(from, error.replace(tagName(), "<regex:TIMEOUT>"));
            return from;
        } catch (Exception e) {
            from = replaceFirstTag(from, error);
            return from;
        }
        from = removeFirstTag(from);
        toReplace.add(new ReplaceObject(splitArgs.get(0), splitArgs.get(1)));
        return from;
    }

    @Override
    public String tagName() {
        return "<regex>";
    }

    @Override
    public int argsRequired() {
        return 2;
    }

    @Override
    public String usage() {
        return "Regex" + splitter + "ReplaceWith";
    }

    @Override
    public String desc() {
        return "Uses a regular expression to replace parts of the command with the second argument.";
    }

    @Override
    public String replaceMode(String from, List<ReplaceObject> toReplace) {
        for (ReplaceObject t : toReplace) {
            try {
                try {
                    from = timedReplace(from, t.getFrom(), t.getTo());
                } catch (IllegalArgumentException e) {
                    from = timedReplace(from, t.getFrom(), prepReplace(t.getTo()));
                }
            } catch (TimedCharSequence.TimeoutException e) {
                from = error.replace(tagName(), "<regex:TIMEOUT>");
            }
        }
        return from;
    }

    public String timedReplace(String str, String pattern, String replacement)
            throws TimedCharSequence.TimeoutException, PatternSyntaxException {
        return timedReplace(str, pattern, replacement, REGEX_TIMEOUT_MS);
    }

    public String timedReplace(String str, String pattern, String replacement, long timeout)
            throws TimedCharSequence.TimeoutException, PatternSyntaxException {
        //obtained from String#replaceAll code
        return Pattern.compile(pattern)
                .matcher(new TimedCharSequence(str, timeout))
                .replaceAll(replacement);
    }
}


/**
 * A self-terminating CharSequence that throws TimedCharSequence.TimeoutException when it existed
 * beyond a specified time. Used for interrupting long-running regexps, for example.
 *
 * @author Lyrth
 */
class TimedCharSequence implements CharSequence {
    private CharSequence inner;  // normal CharSequence
    private final long timeout;

    /**
     * @param inner The CharSequence to handle.
     * @param timeout Time from CharSequence creation to the moment it should self-destruct, in ms.
     * @param exact True if {@param timeout} should instead be an absolute moment in system time.
     */
    private TimedCharSequence(CharSequence inner, long timeout, boolean exact) {
        super();
        this.timeout = exact ? timeout : (System.currentTimeMillis() + timeout);
        this.inner = inner;
    }

    /**
     * @param inner The CharSequence to handle.
     * @param timeout Time from CharSequence creation to the moment it should self-destruct, in ms.
     */
    public TimedCharSequence(CharSequence inner, long timeout) {
        this(inner,timeout,false);
    }

    @Override
    public char charAt(int index) {
        if (System.currentTimeMillis() > timeout)
            throw new TimeoutException();
        return inner.charAt(index);
    }

    @Override
    public int length() {
        return inner.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new TimedCharSequence(inner.subSequence(start, end), timeout, true);
    }

    @Override
    public String toString() {
        return inner.toString();
    }

    public static class TimeoutException extends RuntimeException {
        public TimeoutException(){ super(); }
        public TimeoutException(String message){ super(message); }
        public TimeoutException(String message, Throwable cause) { super(message, cause); }
        public TimeoutException(Throwable cause) { super(cause); }
    }
}
