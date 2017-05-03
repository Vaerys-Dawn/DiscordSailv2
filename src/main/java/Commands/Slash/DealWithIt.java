package Commands.Slash;

import Interfaces.SlashCommand;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class DealWithIt implements SlashCommand {
    @Override
    public String call() {
        return "/DealWithIt";
    }

    @Override
    public String response() {
        return "(•\\_•) ( •\\_•)>⌐■-■ (⌐■_■)";
    }
}
