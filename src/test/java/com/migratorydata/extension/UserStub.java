package com.migratorydata.extension;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserStub implements MigratoryDataPresenceListener.User {

    List<String> subjects;
    String externalToken;
    boolean offline;

    public UserStub(List<String> subjects, String externalToken, boolean offline) {
        this.subjects = subjects;
        this.externalToken = externalToken;
        this.offline = offline;
    }

    @Override
    public String getExternalToken() {
        return externalToken;
    }

    @Override
    public long getSessionId() {
        return 0;
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
        return Collections.EMPTY_MAP;
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
