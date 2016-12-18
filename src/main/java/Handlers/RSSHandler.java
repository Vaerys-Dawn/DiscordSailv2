package Handlers;

import Main.Constants;
import Objects.RSSObject;
import POGOs.RSSFeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Vaerys on 13/12/2016.
 */
public class RSSHandler {

    public static ArrayList<RSSObject> updateRSSFeed(RSSFeed rssFeed) {
        ArrayList<RSSObject> feed = new ArrayList<>();
        try {
            URL rssURL = new URL(rssFeed.getRssURL());
            BufferedReader in = new BufferedReader(new InputStreamReader(rssURL.openStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = in.readLine()) != null){
                builder.append(line + "\n");
            }
            FileHandler.writeToFile(Constants.DIRECTORY_TEMP+ "testing.txt",builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feed;
    }
}
