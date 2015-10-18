package com.nomad_mystic_passwordmanager.passwordmanager;

/**
 * Created by Nomad_Mystic on 5/27/2015.
 */
public class AccountItem {
    private String mName;
    private String mPassword;

    public AccountItem(String name, String password){
        mName = name;
        mPassword = password;
    }

    public String getName() {

        return mName;
    }

    public String   getPassword() {
        return mPassword;
    }

    public String toString() {
        return mName;
    }
}
