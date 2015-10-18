package com.nomad_mystic_crypto.crypto;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static com.nomad_mystic_crypto.crypto.Crypto.decryptString;
import static com.nomad_mystic_crypto.crypto.Crypto.encrypt;
import static com.nomad_mystic_crypto.crypto.Crypto.generateKeyFromPassword;
import static com.nomad_mystic_crypto.crypto.Crypto.generateSalt;
import static com.nomad_mystic_crypto.crypto.Crypto.saltString;


public class MainActivity extends ActionBarActivity {

    public static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            String recovered = savedInstanceState.getString("Password");
            Log.d("MainActivity", recovered);
        }
        String plainText = "This is the plain text";
        String password = "SwordFish";
        try {
            String salt = saltString(generateSalt());
            Crypto.SecretKeys keys = generateKeyFromPassword(password, salt);
            Crypto.CipherTextIvMac cipherText = encrypt(plainText, keys);
            Log.d(LOG_TAG, "Cipher Text" + cipherText.toString());

            String civ = cipherText.toString();

            Crypto.SecretKeys newKeys = generateKeyFromPassword(password, salt);
            Crypto.CipherTextIvMac newCipherText = new Crypto.CipherTextIvMac(civ);

            String newPlainText = decryptString(newCipherText, newKeys);
            Log.d(LOG_TAG, "plainText" + newPlainText);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Password", "Swordfish");

    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("Password", "Swordfish");
//        editor.commit();
//    }
}
