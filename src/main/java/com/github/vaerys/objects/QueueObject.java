package com.github.vaerys.objects;

public class QueueObject {
    long messageId;
    long uID;
    String type;
    boolean markedForRemoval = false;

    public QueueObject(long messageId, long uID, String type) {
        this.messageId = messageId;
        this.uID = uID;
        this.type = type;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getuID() {
        return uID;
    }

    public String getType() {
        return type;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void toggleMarkedForRemoval() {
        markedForRemoval = !markedForRemoval;
    }
}
