package com.github.vaerys.objects;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;

/**
 * Created by Vaerys on 04/11/2016.
 */
public class OffenderObject {
    int count;
    String displayName;
    String ID;

    public long getID() {
        return Utility.stringLong(ID);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public OffenderObject(long ID) {
        this.displayName = Globals.getClient().getUserByID(ID).getName() + "#" + Globals.getClient().getUserByID(ID).getDiscriminator();
        this.ID = ID + "";
        count = 1;
    }

    public void addOffence() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
