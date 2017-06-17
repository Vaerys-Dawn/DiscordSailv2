package Enums;

/**
 * Created by Vaerys on 14/06/2017.
 */
public enum UserSetting {

    //User Settable
    NO_XP_GAIN, //Allows the user to turn of Xp gain for themselves.
    SEND_LVLUP_SERVER, //Sends level up messages to the channel that the message to rank up was made in.
    SEND_LVLUP_CHANNEL, //Will send the Level up to the server set "Rank Chat/Level Up" channel else
    // if no channel exists it will send it to the channel the final message was sent to.
    SEND_LVLUP_DMS, //Sends level up messages to Direct messages.
    DONT_SEND_LVLUP, //makes it so that you don't receive level up messages.

    //Admin Only
    DENIED_XP, //Admin Settable. Denies all xp gain for the user.
    DONT_SHOW_LEADERBOARD, //Hides user on the server leader-board.
    AUTO_SHITPOST, //Automatically sets all new commands made by this user to be shitpost locked.
    DENY_CCS //Removes the ability for the user to make custom commands.
}
