package Main;

import sx.blah.discord.api.IDiscordClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    //CoolDowns
    public static int serverCoolDown = 0;
    public static int doAdminMention = 0;

    //Console Bot message Variables
    public static String consoleMessageCID = "177747821338951680";
    public static String creatorID = "153159020528533505";
    public static String botName = "S.A.I.L";
    public static IDiscordClient client;
    public static int argsMax = 500;
    public static int maxWarnings = 3;
    public static boolean isReady = false;
    public static String version;

    public static IDiscordClient getClient() {
        return client;
    }

    public static void setClient(IDiscordClient client) {
        Globals.client = client;
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            System.out.println(version + " << version");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
