package com.github.vaerys.tags.cctags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.ReplaceObject;
import com.github.vaerys.templates.TagReplaceObject;
import com.github.vaerys.utilobjects.InterruptibleCharSequence;

import java.util.List;
import java.util.regex.Pattern;

public class TagRegex extends TagReplaceObject {

    public TagRegex(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, List<ReplaceObject> toReplace) {
        List<String> splitArgs = getSplit(from);
        String test = "Testing.";
        try {
            test.replaceAll(splitArgs.get(0), test);
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
                from = Pattern.compile(t.getFrom()).matcher(new InterruptibleCharSequence(from)).replaceAll(t.getTo());
            } catch (IllegalArgumentException e) {
                from = Pattern.compile(t.getFrom()).matcher(new InterruptibleCharSequence(from)).replaceAll(prepReplace(t.getTo()));
            }
        }
        return from;
    }
}
