package becker.andy.map2018.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("pref_file", Context.MODE_PRIVATE);
    }

    public void writeLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login_status", status);
        editor.commit();
    }

    public void writeUser(String user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", user);
        editor.commit();
    }

    public String readUser() {
        return sharedPreferences.getString("user", "none");
    }

    public boolean readLoginStatus() {
        return sharedPreferences.getBoolean("login_status", false);
    }

    public void writeUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.commit();
    }

    public int readUserId() {
        return sharedPreferences.getInt("userId", 0);
    }

    public void writeInsti(String insti) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("insti", insti);
        editor.commit();
    }

    public String readInsti() {
        return sharedPreferences.getString("insti", "none");
    }

    public void writeEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public String readEmail() {
        return sharedPreferences.getString("email", "none");
    }

}
