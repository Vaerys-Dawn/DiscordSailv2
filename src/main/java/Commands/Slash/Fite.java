package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class Fite implements SlashCommand {
    @Override
    public String call() {
        return "/Fite";
    }

    @Override
    public String response() {
        return "(ง'̀-'́)ง";
    }
}
