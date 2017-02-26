package Commands.Characters.EditModes;

import Objects.CharacterObject;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharGender {

    public static String execute(String args, CharacterObject character) {
        if (args.length() > 20) {
            return "> Gender Must be under 20 characters.";
        }
        character.setGender(args);
        return "> Gender Updated.";
    }
}
