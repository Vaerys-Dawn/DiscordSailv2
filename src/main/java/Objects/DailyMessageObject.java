package Objects;

import Main.TagSystem;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Vaerys on 14/01/2017.
 */
public class DailyMessageObject {

    DayOfWeek dayOfWeek;
    List<String> contents;
    String fileName;

    public DailyMessageObject(DayOfWeek dayOfWeek, String contents, String fileName) {
        this.dayOfWeek = dayOfWeek;
        this.contents = Arrays.asList(contents.split("\n"));
        this.fileName = fileName.replace("#day#", dayOfWeek.toString());
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getContents() {
        StringBuilder builder = new StringBuilder();
        for (String s : contents) {
            builder.append(s + "\n");
        }
        return TagSystem.tagRandom(TagSystem.tagSpacer(TagSystem.tagNoNL(TagSystem.tagMentionRemover(builder.toString()))));
    }

    public String getFileName() {
        return fileName;
    }
}
