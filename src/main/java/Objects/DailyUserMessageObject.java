package Objects;

import Main.TagSystem;

import java.time.DayOfWeek;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class DailyUserMessageObject {
    String author;
    long userID = -1;
    String content;
    DayOfWeek day;
    long uID = -1;

    public DailyUserMessageObject(String description, DayOfWeek day, long userID, long uID) {
        this.content = description;
        this.day = day;
        this.userID = userID;
        this.uID = uID;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getContents() {
        return TagSystem.tagRandom(TagSystem.tagSpacer(TagSystem.tagNoNL(TagSystem.tagMentionRemover(content))));
    }

    public long getUID() {
        return uID;
    }

    public void setContents(String contents) {
        this.content = contents;
    }

    public long getUserID() {
        return userID;
    }
}
