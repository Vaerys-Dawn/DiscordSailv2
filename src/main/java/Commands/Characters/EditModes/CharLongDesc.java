package Commands.Characters.EditModes;

import Objects.CharacterObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 26/02/2017.
 */
public class CharLongDesc {
    public static String execute(String args, CharacterObject character) {
        try {
            new URL(args);
            character.setLongBioURL(args);
            return "> Long Desc Link updated.";
        } catch (MalformedURLException e) {
            return "> Specified URL is invalid.";
        }
    }
}
