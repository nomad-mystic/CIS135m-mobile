package com.nomad_mystic_passwordmanager.passwordmanager;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static com.nomad_mystic_passwordmanager.passwordmanager.Crypto.decryptString;
import static com.nomad_mystic_passwordmanager.passwordmanager.Crypto.encrypt;
import static com.nomad_mystic_passwordmanager.passwordmanager.Crypto.generateKeyFromPassword;
import static com.nomad_mystic_passwordmanager.passwordmanager.Crypto.generateSalt;
import static com.nomad_mystic_passwordmanager.passwordmanager.Crypto.saltString;


public class MainActivity extends ActionBarActivity
        implements AccountItemFragment.OnFragmentInteractionListener,
        AccountViewFragment.OnAccountView,
        DeleteConfirmFragment.OnDeleteConfirmListener {

    private MenuItem mAddItem;
    private MenuItem mRemoveItem;
    private AccountItemFragment mAccountList;
    private String mAccountCipherText = null;
    private String mSalt = null;
    private String mPassword = null;

    private static final String LOG_TAG = "MainActivity";
    private static final String CIPHER_TAG = "account_cipher";
    private static final String SALT_TAG = "salt";
    private static final String PASSWORD_TAG = "password_cipher";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mSalt = prefs.getString(SALT_TAG, null);
        mAccountCipherText = prefs.getString(CIPHER_TAG, null);

        if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.list_Fragment, new LoginButtonFragment());
            transaction.add(R.id.content_Fragment, new WelcomeFragment());
            transaction.commit();
        } else {
            String cipherPassword = savedInstanceState.getString(PASSWORD_TAG);
            if(cipherPassword != null && mSalt != null) {
                try {
                    Crypto.SecretKeys keys = Crypto.generateKeyFromPassword(mSalt, mSalt);
                    Crypto.CipherTextIvMac civ = new Crypto.CipherTextIvMac(cipherPassword);
                    mPassword = Crypto.decryptString(civ, keys);
                    Crypto.SecretKeys accountKeys = Crypto.generateKeyFromPassword(mPassword, mSalt);
                    Crypto.CipherTextIvMac accountCiv = new Crypto.CipherTextIvMac(mAccountCipherText);
                    String accounts = Crypto.decryptString(accountCiv, accountKeys);

                    mAccountList = new AccountItemFragment();
                    mAccountList.setAccounts(accounts);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.list_Fragment, mAccountList);
                    transaction.commit();

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void hideMenuItem(MenuItem item) {
        item.setVisible(false);
    }
    public void showMenuItem(MenuItem item) {
        item.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mAddItem = menu.findItem(R.id.action_add);
        mRemoveItem = menu.findItem(R.id.action_delete);
        hideMenuItem(mAddItem);
        hideMenuItem(mRemoveItem);

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
        } else if(id == R.id.action_add) {
            replaceFragment(R.id.content_Fragment, new AddAccountFragment());
        } else if(id == R.id.action_delete) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DeleteConfirmFragment fragment = new DeleteConfirmFragment();
            fragment.show(fragmentManager, "Delete Confirm");

        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(int resId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(resId, fragment);
        transaction.commit();
    }

    public void loginClick(View button) {

        replaceFragment(R.id.content_Fragment, new LoginFragment());
    }

    public void loginCancel(View button) {

        replaceFragment(R.id.content_Fragment, new WelcomeFragment());
    }
    public void addAccountCancel(View button) {
        replaceFragment(R.id.content_Fragment, new WelcomeFragment());
    }
    public void addAccountSubmit(View button) {
        EditText nameEdit = (EditText) findViewById(R.id.add_account_name);
        EditText passwordEdit = (EditText) findViewById(R.id.add_account_password);

        String name = nameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        AccountItem item = new AccountItem(name, password);
        mAccountList.addAccount(item);
        onFragmentInteraction(item);


    }
    private boolean validPassword(String password) {

        if(mSalt == null) {
            mPassword = password;
            mAccountList =  new AccountItemFragment();
            replaceFragment(R.id.content_Fragment, new LoggedInFragment());
            replaceFragment(R.id.list_Fragment,mAccountList);

            return true;

        } else {
            try {
                Crypto.SecretKeys keys = generateKeyFromPassword(password, mSalt);
                Crypto.CipherTextIvMac civ = new Crypto.CipherTextIvMac(mAccountCipherText);
                String account = decryptString(civ, keys);
                mPassword = password;
                mAccountList =  new AccountItemFragment();
                mAccountList.setAccounts(account);
                replaceFragment(R.id.content_Fragment, new LoggedInFragment());
                replaceFragment(R.id.list_Fragment,mAccountList);
                return true;
            } catch (GeneralSecurityException e) {
                return false;
            } catch (UnsupportedEncodingException e) {
                return false;
            }
        }
    }

    public void loginSubmit(View button) {
        EditText passwordText = (EditText) findViewById(R.id.password_edit_login);
        String password = passwordText.getText().toString();
        boolean isValid = validPassword(password);

        if(isValid) {
            showMenuItem(mAddItem);

        } else {
            replaceFragment(R.id.content_Fragment, new SorryFragment());
        }
    }

    @Override
    public void onFragmentInteraction(AccountItem item) {
        AccountViewFragment fragment = new AccountViewFragment();
        fragment.setAccount(item);
        replaceFragment(R.id.content_Fragment, fragment);
    }

    @Override
    public void onShowDelete() {
        showMenuItem(mRemoveItem);
    }

    @Override
    public void onHideDelete() {
        hideMenuItem((mRemoveItem));
    }

    @Override
    public void onDeleteConfirm() {
        mAccountList.removeCurrentItem();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mPassword != null) {
            try {
                String salt = saltString(generateSalt());
                Crypto.SecretKeys keys = generateKeyFromPassword(salt, salt);
                String cipherPass = encrypt(mPassword, keys).toString();
                String account = mAccountList.getAccounts();
                Crypto.SecretKeys accountKeys = generateKeyFromPassword(mPassword, salt);
                String cipherText = encrypt(account, accountKeys).toString();

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SALT_TAG, salt);
                editor.commit();
                savedInstanceState.putString(PASSWORD_TAG, cipherPass);
                editor.putString(CIPHER_TAG, cipherText);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {

        if(mPassword != null) {
            try {
                String salt = saltString(generateSalt());
                String account = mAccountList.getAccounts();
                Crypto.SecretKeys accountKeys = generateKeyFromPassword(mPassword, salt);
                String cipherText = encrypt(account, accountKeys).toString();

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SALT_TAG, salt);
                editor.commit();
                editor.putString(CIPHER_TAG, cipherText);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
