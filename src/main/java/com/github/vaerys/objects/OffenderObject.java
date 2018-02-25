package com.github.vaerys.objects;

/**
 * Created by Vaerys on 04/11/2016.
 */
public class OffenderObject {
    int count;
    long id;

    public OffenderObject(long id) {
        this.id = id;
        count = 1;
    }

    public long getID() {
        return id;
    }

    public void addOffence() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
