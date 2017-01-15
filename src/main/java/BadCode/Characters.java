package BadCode;

import Objects.CharacterObject;
import Objects.RoleTypeObject;
import Main.Globals;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 27/07/2016.
 */
public class Characters {
    Boolean wasLoaded = false;
    ArrayList<String[]> charList = new ArrayList<>();

//    "test",
//    "153159020528533505",
//    "[Admin] Dawn Felstar",
//    "176668551556497408"

    //name = 0
    //userID = 1
    //roleID's = 3 and oh boy is this gunna be fun....

    public ArrayList<CharacterObject> transferCharacters() {
        ArrayList<CharacterObject> characters = new ArrayList<>();
        for (String[] s : charList) {
            IUser author = Globals.getClient().getUserByID(s[1]);
            if (author != null) {
                String username = s[2];
                String[] roleIDs = s[3].split(Pattern.quote(","));
                ArrayList<RoleTypeObject> roles = new ArrayList<>();
                for (String role : roleIDs
                        ) {
                    roles.add(new RoleTypeObject(Globals.getClient().getRoleByID(role).getName(),role));
                }
                characters.add(new CharacterObject(s[0], s[1], username, roles));
            }
        }
        return characters;
    }
}
