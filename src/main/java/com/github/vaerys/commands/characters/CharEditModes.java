package com.github.vaerys.commands.characters;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.CharacterObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 04/04/2017.
 */
public class CharEditModes {

    public static String age(String args, CharacterObject character) {
        if (args.length() > 20) {
            return "> Character age length must be under 20 characters.";
        } else {
            character.setAge(args);
            return "> Age Updated";
        }
    }

    public static String gender(String args, CharacterObject character) {
        if (args.length() > 20) {
            return "> Gender Must be under 20 characters.";
        }
        character.setGender(args);
        return "> Gender Updated.";
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

    public static String desc(String args, CharacterObject character) {
        if (args.length() > 300) {
            return "> Character Description must be under 300 characters.";
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
