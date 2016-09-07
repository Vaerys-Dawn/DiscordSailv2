package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class BlackListObject {
    public String phrase;
    String reason;

    public BlackListObject(String phrase, String reason) {
        this.phrase = phrase;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
