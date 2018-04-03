package com.github.vaerys.commands.characters;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 04/04/2017.
 */
public class CharEditModes {

    private static String getOverDraw(long amount) {
        if (amount == 1) return "(over by " + amount + " character)";
        return "(over by " + amount + " characters)";
    }

    public static String age(String args, CharacterObject character, CommandObject command) {
        long maxChars = 20;
        if (command.user.isPatron) maxChars += maxChars;
        if (args.length() > maxChars) {
            long overDraw = args.length() - maxChars;
            return "> Character age length must be under " + maxChars + " characters. " + getOverDraw(overDraw);
        } else if (args.contains("\n")) {
            return "> Character age cannot contain newlines.";
        } else {
            character.setAge(args);
            return "> Age Updated";
        }
    }

    public static String gender(String args, CharacterObject character, CommandObject command) {
        long maxChars = 20;
        if (command.user.isPatron) maxChars += maxChars;
        if (args.length() > maxChars) {
            long overDraw = args.length() - maxChars;
            return "> Gender Must be under " + maxChars + " characters. " + getOverDraw(overDraw);
        } else if (args.contains("\n")) {
            return "> Character gender cannot contain newlines.";
        } else {
            character.setGender(args);
            return "> Gender Updated";
        }
    }

    public static String height(String args, CharacterObject character, CommandObject command) {
        long maxChars = 20;
        if (command.user.isPatron) maxChars += maxChars;
        if (args.length() > maxChars) {
            long overDraw = args.length() - maxChars;
            return "> Character height Must be under " + maxChars + " characters. " + getOverDraw(overDraw);
        } else if (args.contains("\n")) {
            return "> Character height cannot contain newlines.";
        } else {
            character.setHeight(args);
            return "> Height Updated";
        }
    }

    public static String weight(String args, CharacterObject character, CommandObject command) {
        long maxChars = 20;
        if (command.user.isPatron) maxChars += maxChars;
        if (args.length() > maxChars) {
            long overDraw = args.length() - maxChars;
            return "> Character weight Must be under " + maxChars + " characters. " + getOverDraw(overDraw);
        } else if (args.contains("\n")) {
            return "> Character weight cannot contain newlines.";
        } else {
            character.setWeight(args);
            return "> Weight Updated";
        }
    }

    public static String avatar(String args, CharacterObject character, CommandObject command) {
        if (args.contains(" ") || args.contains("\n")) {
            return "> Image URL specified is invalid.";
        }
        if (args == null || args.isEmpty()) {
            if (command.message.getAttachments().size() != 0) {
                args = command.message.getAttachments().get(0).getUrl();
            }
        }
        if (Utility.isImageLink(args)) {
            character.setAvatarURL(args);
            return "> Character Avatar Updated.";
        } else {
            return "> Image URL specified is invalid.";
        }
    }

    public static String desc(String args, CharacterObject character, CommandObject command) {
        long maxChars = 300;
        long maxNewlines = 5;
        if (command.user.isPatron) {
            maxChars += maxChars;
            maxNewlines += maxNewlines;
        }
        long newlineCount = StringUtils.countMatches(args, "\n");
        if (args.length() > maxChars) {
            long overDraw = args.length() - maxChars;
            return "> Character Description must be under " + maxChars + " characters. " + getOverDraw(overDraw);
        } else if (newlineCount > maxNewlines) {
            long overdraw = newlineCount - maxNewlines;
            return "> Character Description has too many Newline characters (over by " + overdraw + " newlines)";
        } else {
            character.setShortBio(args);
            return "> Description Updated.";
        }
    }

    public static String longDesc(String args, CharacterObject character) {
        try {
            new URL(args);
            if (Utility.checkURL(args)) {
                character.setLongBioURL(args);
                return "> Long Desc Link updated.";
            } else {
                return "> Specified URL is invalid.";
            }
        } catch (MalformedURLException e) {
            return "> Specified URL is invalid.";
        }
    }
}
