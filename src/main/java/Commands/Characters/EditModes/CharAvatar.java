package Commands.Characters.EditModes;

import Main.Utility;
import Objects.CharacterObject;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharAvatar {

    public static String execute(String args, CharacterObject character) {
        if (args.contains(" ") || args.contains("\n")) {
            return "> Image URL specified is invalid.";
        }
        if (Utility.isImageLink(args)) {
            character.setAvatarURL(args);
            return "> Character Avatar Updated.";
        } else {
            return "> Image URL specified is invalid.";
        }
    }

}
