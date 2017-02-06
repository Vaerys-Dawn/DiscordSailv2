package Objects;

import java.util.regex.Pattern;

/**
 * Created by Vaerys on 15/01/2017.
 */
public class SplitFirstObject {
    String firstWord = null;
    String rest = null;
    public SplitFirstObject(String from){
        String[] splitFrom = from.split(" ");
        if (splitFrom.length != 0 || splitFrom != null){
            firstWord = splitFrom[0];
            if (splitFrom.length == 1){
                return;
            }else {
                rest = from.replaceFirst(Pattern.quote(firstWord + " "),"");
            }
        }
    }

    public String getFirstWord() {
        return firstWord;
    }

    public String getRest() {
        return rest;
    }

    public void editRestReplace(String from, String to) {
        if (rest != null){
            rest = rest.replace(from,to);
        }
    }
}
