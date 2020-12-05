package net.okhotnikov.harald.service;

import android.content.SharedPreferences;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.model.User;

import static android.content.Context.MODE_PRIVATE;

public class LocalPersistence {
    public static final String PERSON_KEY = "harold person";
    public static final String USER_DETAILS = "user details";
    private final SharedPreferences prefs;
    private final MainActivity activity;

    public LocalPersistence(MainActivity activity) {
        this.prefs = activity.getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        this.activity = activity;
    }

    public void save(User user){
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString(PERSON_KEY,user.person);
        editor.apply();
    }

    public User load(){
        User user = new User();
        user.person = prefs.getString(PERSON_KEY,"Anonymous");
        return user;
    }
}
