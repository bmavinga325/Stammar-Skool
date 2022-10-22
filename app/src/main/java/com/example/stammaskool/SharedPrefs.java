package com.example.stammaskool;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private Context context;
    SharedPreferences sharedPref;

    public SharedPrefs(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("SHARED_PREFS",Context.MODE_PRIVATE);

    }


    public String userId() { return sharedPref.getString("userId", "");}

    public void setUserId(String value) { sharedPref.edit().putString("userId", value).apply(); }


    public String userName() { return sharedPref.getString("userName", "");}

    public void setUserName(String value) { sharedPref.edit().putString("userName", value).apply(); }


    public String userEmail() { return sharedPref.getString("userEmail", "");}

    public void setUserEmail(String value) { sharedPref.edit().putString("userEmail", value).apply(); }


    public String userImage() { return sharedPref.getString("userImage", "Default");}

    public void setUserImage(String value) { sharedPref.edit().putString("userImage", value).apply(); }


    public String userPhone() { return sharedPref.getString("userPhone", "");}

    public void setUserPhone(String value) { sharedPref.edit().putString("userPhone", value).apply(); }





}