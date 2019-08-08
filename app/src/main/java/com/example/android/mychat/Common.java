package com.example.android.mychat;
public class Common {
    public static User currentUser;

    public Common() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Common.currentUser = currentUser;
    }
}
