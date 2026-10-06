package services;

import models.User;

/**
 * Session service - manages the current logged-in user
 * Simple singleton pattern
 */
public class SessionService {
    private static SessionService instance;
    private User currentUser;

    private SessionService() {}

    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public boolean isTeacher() {
        return currentUser != null && "teacher".equals(currentUser.getRole());
    }

    public boolean isStudent() {
        return currentUser != null && "student".equals(currentUser.getRole());
    }

    public void clearSession() {
        this.currentUser = null;
    }
}
