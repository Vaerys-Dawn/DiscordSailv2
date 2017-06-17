package Handlers;

import Interfaces.*;
import Main.Constants;
import Main.Globals;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Vaerys on 31/05/2017.
 */
public class WikiBuilder {

    public static void handleCommandLists() {
        ArrayList<Command> commands = Globals.getCommands();
        ArrayList<ChannelSetting> channels = Globals.getChannelSettings();
        ArrayList<GuildToggle> toggles = Globals.getGuildGuildToggles();
        ArrayList<String> types = Globals.getCommandTypes();
        ArrayList<DMCommand> dmCommands = Globals.getCommandsDM();
        ArrayList<SlashCommand> slashCommands = Globals.getSlashCommands();
        IDiscordClient client = Globals.getClient();
        File commandMenu = new File(Constants.DIRECTORY_WIKI + "Command-Menu.md");
        String pagePath = "https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/";
        FileHandler.createDirectory(Constants.DIRECTORY_WIKI_COMMANDS);

        String menuContents = "";
        menuContents += "### Command Types:   \n  \n";
        for (String s : types) {
            File temp = new File(Constants.DIRECTORY_WIKI_COMMANDS + s + ".md");
            String contents = "";
            menuContents += "[" + s + "](" + pagePath + s + ")  \n";
            if (!temp.exists()){
                try {
                    temp.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            contents += "### " + s + "\n  \n  ";
            for (Command c : commands) {
                if (c.type().equalsIgnoreCase(s)) {
                    contents += "##" +c.names()[0] +"\n";
                }
            }
        }
        System.out.println("Creating Wiki Menu");
        FileHandler.writeToFile(commandMenu.getPath(), menuContents, true);
    }
}
