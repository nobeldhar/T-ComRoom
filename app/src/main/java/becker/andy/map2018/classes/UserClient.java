package becker.andy.map2018.classes;

import android.app.Application;

import becker.andy.map2018.models.User;

public class UserClient extends Application {
    private User user=null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
