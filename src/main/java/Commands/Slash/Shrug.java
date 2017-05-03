package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Shrug implements SlashCommand {
    @Override
    public String call() {
        return "/Shrug";
    }

    @Override
    public String response() {
        return "¯\\_(ツ)_/¯";
    }
}
