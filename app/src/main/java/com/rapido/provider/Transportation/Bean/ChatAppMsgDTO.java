package com.rapido.provider.Transportation.Bean;

public class ChatAppMsgDTO {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";
    private String msgTime;
    // Message content.
    private String msgContent;
    // Message type.
    private String msgType;

    public ChatAppMsgDTO(String msgType, String msgContent, String msgTime) {
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.msgTime = msgTime;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
