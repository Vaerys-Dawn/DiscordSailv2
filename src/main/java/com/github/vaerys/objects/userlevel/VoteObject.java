package com.github.vaerys.objects.userlevel;

import java.util.ArrayList;

/**
 * Created by Vaerys on 28/03/2017.
 */
public class VoteObject {
    String userID;
    ArrayList<String> votes = new ArrayList<>();

    public VoteObject(String userID, ArrayList<String> votes) {
        this.userID = userID;
        this.votes = votes;
    }
}
