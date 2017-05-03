package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Gib implements SlashCommand {
    @Override
    public String call() {
        return "/Gib";
    }

    @Override
    public String response() {
        return "༼ つ ◕_◕ ༽つ";
    }
}
