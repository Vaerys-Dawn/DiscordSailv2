package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class TableFlip implements SlashCommand {
    @Override
    public String call() {
        return "/TableFlip";
    }

    @Override
    public String response() {
        return "(╯°□°）╯︵ ┻━┻";
    }
}
