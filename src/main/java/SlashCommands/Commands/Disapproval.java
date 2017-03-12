package SlashCommands.Commands;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Disapproval implements SlashCommand {
    @Override
    public String call() {
        return "/Disapprove";
    }

    @Override
    public String response() {
        return "ಠ_ಠ";
    }
}
