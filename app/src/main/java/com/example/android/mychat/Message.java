package com.example.android.mychat;

public class Message {
   String message,type,sender;
   long time;
   boolean seen;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Message(String message, String type, String sender, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.sender = sender;
        this.time = time;
        this.seen = seen;
    }

    public Message() {
    }
}
