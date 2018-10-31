package com.github.vaerys.objects.adminlevel;

/**
 * Written by AndrielChaoti 24-Dec-2017
 */
public class ModNoteObject {

    private String note;
    private long creatorId;
    private long timestamp;
    private long lastEditedTimestamp = -1;
    private long editorId = -1;
    private boolean isStrike = false;

    /**
     * @param note  getContents of the note.
     * @param modId the userId of the creator of the note.
     * @param time  timestamp epoch second value.
     */
    public ModNoteObject(String note, long modId, long time) {
        this.note = note;
        this.creatorId = modId;
        this.timestamp = time;
    }

    public ModNoteObject(String note, long modId, long time, boolean isStrike) {
        this.note = note;
        this.creatorId = modId;
        this.timestamp = time;
        this.isStrike = isStrike;
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

    public boolean getStrike() {
        return this.isStrike;
    }

    public void setStrike(boolean strike) {
        this.isStrike = strike;
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
