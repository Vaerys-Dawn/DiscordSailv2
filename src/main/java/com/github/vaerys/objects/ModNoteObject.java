package com.github.vaerys.objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Written by AndrielChaoti 24-Dec-2017
 */
public class ModNoteObject {

    private String note;
    private long creatorId;
    private long timestamp;
    private long lastEditedTimestamp = -1;
    private long editorId = -1;

    public ModNoteObject(String note, long modId, long time) {
        this.note = note;
        this.creatorId = modId;
        this.timestamp = time;
    }

    public void editNote(String note, long modId, long time) {
        this.lastEditedTimestamp = time;
        this.editorId = modId;
        this.note = note;
    }

    //region Getters+Setters
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getLastEditedTimestamp() {
        return lastEditedTimestamp;
    }

    public long getEditorId() {
        return editorId;
    }
    //endregion
}
