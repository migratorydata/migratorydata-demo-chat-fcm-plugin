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
    private Map<String, User> userByToken = new HashMap<>(); // fcmToken -> user
    private Map<String, Set<User>> usersBySubject = new HashMap<>(); // subject -> list of users

    /**
     * Updates the presence information for a user.
     *
     * @param user The user object containing updated presence information.
     */
    public void update(User user) {
        String fcmToken = user.getExternalToken();

        User cachedUser = userByToken.get(fcmToken);
        if (cachedUser != null) {
            if (user.isOffline() && cachedUser.isOffline()) {
                // received an offline presence event for a user who had previously been marked as offline
                if (isPresenceEventBehind(user, cachedUser)) {
                    return;
                }
            } else if (!user.isOffline() && !cachedUser.isOffline()) {
                // received an online presence event for a user who had previously been marked as online
                if (cachedUser.getSessionId() != user.getSessionId()) {
                    if (isPresenceEventBehind(user, cachedUser)) {
                        return;
                    }
                }
            } else {
                if (user.isOffline()) {
                    // received an offline presence event for a user who had previously been marked as online
                    if (user.getSessionId() != cachedUser.getSessionId()) {
                        if (isPresenceEventBehind(user, cachedUser)) {
                            return;
                        }
                    }
                } else {
                    // received an online presence event for a user who had previously been marked as offline
                    if (user.getSessionId() == cachedUser.getSessionId()) {
                        // already received disconnect event
                        return;
                    } else {
                        if (isPresenceEventBehind(user, cachedUser)) {
                            return;
                        }
                    }
                }
            }
        }

        // assign the new user presence event to the fcmToken of the current user
        userByToken.put(fcmToken, user);

        // add received user to the list of users for each of its subscribed subjects
        for (String subject : user.getSubjects()) {
            Set<User> userlist = usersBySubject.get(subject);
            if (userlist == null) {
                userlist = new HashSet<>();
                usersBySubject.put(subject, userlist);
            }
            userlist.remove(user);
            userlist.add(user);
        }
    }

    /**
     * Retrieves the list of offline users for a given subject.
     *
     * @param subject The subject for which offline users are requested.
     * @return A list of offline users.
     */
    public List<User> getOfflineUsers(String subject) {
        List<User> offlineUsers = new ArrayList<>();

        Set<User> users = usersBySubject.get(subject);
        if (users != null) {
            Iterator<User> it = users.iterator();
            while (it.hasNext()) {
                User user = it.next();

                // Since the presence API doesn't notify when users subscribe or unsubscribe from subjects, we rely on
                // the list of subjects subscribed by each user provided in their latest presence event. If a user is listed
                // for a subject they're no longer subscribed to (due to unsubscribing), we remove their entry from the
                // list associated with that subject.
                if (!user.getSubjects().contains(subject)) {
                    it.remove(); // lazy cleanup for unsubscriptions
                    continue;
                }

                if (user.isOffline()) {
                    offlineUsers.add(user);
                }
            }
        }

        return offlineUsers;
    }

    /**
     * Retrieves a user based on the provided token.
     *
     * @param token The token associated with the user.
     * @return The user object corresponding to the provided token, or null if not found.
     */
    public User getUserByToken(String token) {
        return userByToken.get(token);
    }

    /**
     * Checks if a presence event for a user is behind another presence event for the same user.
     *
     * @param user The user object representing the current presence event.
     * @param cachedUser The user object representing the cached presence event.
     * @return true if the current presence event is behind the cached presence event, otherwise false.
     */
    private boolean isPresenceEventBehind(User user, User cachedUser) {
        Integer monotonicId = (Integer) user.getAdditionalInfo().get("monotonicId");
        Integer cachedMonotonicId = (Integer) cachedUser.getAdditionalInfo().get("monotonicId");
        if (monotonicId != null && cachedMonotonicId != null) {
            if (monotonicId < cachedMonotonicId) {
                return true; // ignore this presence event because it is an old event
            }
        }
        return false;
    }
}
