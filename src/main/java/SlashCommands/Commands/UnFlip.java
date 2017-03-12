package SlashCommands.Commands;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class UnFlip implements SlashCommand {
    @Override
    public String call() {
        return "/UnFlip";
    }

    @Override
    public String response() {
        return "┬─┬ ノ( ゜-゜ノ)";
    }
}
