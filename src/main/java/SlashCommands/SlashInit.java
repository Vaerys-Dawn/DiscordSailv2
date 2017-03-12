package SlashCommands;

import Interfaces.SlashCommand;
import SlashCommands.Commands.*;

import java.util.ArrayList;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class SlashInit {

    public static ArrayList<SlashCommand> get(){
        ArrayList<SlashCommand> commands = new ArrayList<>();

        commands.add(new Disapproval());
        commands.add(new Lenny());
        commands.add(new TableFlip());
        commands.add(new UnFlip());
        commands.add(new Shrug());
        commands.add(new Gib());
        commands.add(new Fite());
        commands.add(new DealWithIt());

        return commands;
    }
}
