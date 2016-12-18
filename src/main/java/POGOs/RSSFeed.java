package POGOs;

import Objects.RSSObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/12/2016.
 */
public class RSSFeed {
    ArrayList<RSSObject> feed = new ArrayList<>();
    String rssURL;

    public RSSFeed(String rssURL) {
        this.rssURL = rssURL;
    }

    public String getRssURL() {
        return rssURL;
    }
}
