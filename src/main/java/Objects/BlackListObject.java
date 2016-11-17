package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class BlackListObject {
    private String phrase;
    private String reason;

    public BlackListObject(String phrase, String reason) {
        this.phrase = phrase;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getPhrase() {
        return phrase;
    }
}
