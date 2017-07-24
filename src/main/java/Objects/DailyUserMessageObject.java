package Objects;

import Main.TagSystem;

import java.time.DayOfWeek;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class DailyUserMessageObject {
    String author;
    String content;
    DayOfWeek day;

    public DailyUserMessageObject(String description, DayOfWeek day, String name) {
        this.content = description;
        this.day = day;
        this.author = name;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getContents() {
        return TagSystem.tagRandom(TagSystem.tagSpacer(TagSystem.tagNoNL(TagSystem.tagMentionRemover(content))));
    }
}
