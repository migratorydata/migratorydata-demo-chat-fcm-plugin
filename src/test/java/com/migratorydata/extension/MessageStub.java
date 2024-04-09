package com.migratorydata.extension;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;

import java.util.Map;

public class MessageStub implements MigratoryDataPresenceListener.Message {

    String subject;
    byte[] content;

    public MessageStub(String subject, byte[] content) {
        this.subject = subject;
        this.content = content;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getAdditionalInfo() {
        return null;
    }
}
