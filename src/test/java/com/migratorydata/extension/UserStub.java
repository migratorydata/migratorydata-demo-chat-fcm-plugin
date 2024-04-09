package com.migratorydata.extension;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStub implements MigratoryDataPresenceListener.User {

    private int sessionId;
    List<String> subjects;
    String externalToken;
    boolean offline;

    Map<String, Object> additionalInfo = new HashMap<>();

    public UserStub(List<String> subjects, String externalToken, Integer monotonicId, boolean offline, int sessionId) {
        this.subjects = subjects;
        this.externalToken = externalToken;
        this.additionalInfo.put("monotonicId", monotonicId);
        this.offline = offline;
        this.sessionId = sessionId;
    }

    @Override
    public String getExternalToken() {
        return externalToken;
    }

    @Override
    public long getSessionId() {
        return sessionId;
    }

    @Override
    public List<String> getSubjects() {
        return subjects;
    }

    @Override
    public boolean isOffline() {
        return offline;
    }

    @Override
    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserStub) {
            UserStub other = (UserStub) obj;
            return other.externalToken.equals(this.externalToken);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return externalToken.hashCode();
    }
}
