package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Lenny implements SlashCommand {
    @Override
    public String call() {
        return "/Lenny";
    }

    @Override
    public String response() {
        return "( ͡° ͜ʖ ͡°)";
    }
}
