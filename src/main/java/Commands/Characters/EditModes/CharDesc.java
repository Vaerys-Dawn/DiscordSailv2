package Commands.Characters.EditModes;

import Objects.CharacterObject;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharDesc {
    public static String execute(String args, CharacterObject character){
        if (args.length() > 300){
            return "> Character Description must be under 300 characters.";
        }else {
            character.setShortBio(args);
            return "> Description Updated.";
        }
    }
}
