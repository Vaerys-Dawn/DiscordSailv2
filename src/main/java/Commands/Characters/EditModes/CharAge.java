package Commands.Characters.EditModes;

import Objects.CharacterObject;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharAge {

    public static String execute(String args, CharacterObject character){
        if (args.length() > 20){
            return "> Character Age length must be under 20 characters.";
        }else {
            character.setAge(args);
            return "> Age Updated";
        }
    }
}
