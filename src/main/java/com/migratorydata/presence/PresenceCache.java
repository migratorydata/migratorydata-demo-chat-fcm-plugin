package com.migratorydata.presence;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener.User;

import java.util.*;

/**
 * In this example, presence information about users and subscriptions is stored in memory. You
 * might replace or supplement this with some persistent storage.
 *
 * @ThreadSafe The methods of this class are always called from the same thread
 */
public class PresenceCache {
    private Map<String, User> userByToken = new HashMap<>(); // fcm token -> user
    private Map<String, Set<String>> subjectsByToken = new HashMap<>(); // user's token -> subjects subscribed by user
    private Map<String, Set<String>> tokensBySubject = new HashMap<>(); // subject -> tokens of users subscribed to subject

    /**
     * Updates the presence information for a user.
     *
     * @param user The user object containing updated presence information.
     */
    public void update(User user) {
        String token = user.getExternalToken();
        List<String> subjects = user.getSubjects();

        User cachedUser = userByToken.get(token);
        List<String> cachedSubjects = new ArrayList<>();
        if (cachedUser != null) {
            cachedSubjects = cachedUser.getSubjects();
        }

        // If a cached subject is no longer found in the subjects of the new user presence event, it means that the user
        // has unsubscribed from that subject, and we remove the user's token from the subscribers of that subject.
        for (String cachedSubject : cachedSubjects) {
            if (!subjects.contains(cachedSubject)) {
                Set<String> tokens = tokensBySubject.get(cachedSubject);
                tokens.remove(token);
            }
        }

        // If a subject of the new user presence event is not present in the cached subjects of the user, then add the
        // user's token to the subscribers of that subject.
        for (String subject : subjects) {
            if (!cachedSubjects.contains(subject)) {
                Set<String> tokens = tokensBySubject.get(subject);
                if (tokens == null) {
                    tokens = new HashSet<>();
                    tokensBySubject.put(subject, tokens);
                }
                tokens.add(token);
            }
        }

        // Associate the new user presence object with the user's token.
        userByToken.put(token, user);
    }

    /**
     * Get the tokens of the offline users who are subscribed to the provided subject.
     *
     * @param subject The subject to get the tokens of the offline users subscribed to it.
     * @return A list containing the tokens of the offline users subscribed to the provided subject.
     */
    public List<String> getOfflineUserTokens(String subject) {
        List<String> offlineUserTokens = new ArrayList<>();

        Set<String> tokens = tokensBySubject.get(subject);
        if (tokens != null) {
            for (String token : tokens) {
                User user = userByToken.get(token);
                if (user != null && user.isOffline()) {
                    offlineUserTokens.add(token);
                }
            }
        }

        return offlineUserTokens;
    }

}
