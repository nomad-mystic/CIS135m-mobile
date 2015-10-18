package com.nomad_mystic_lab1.lab1_activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToTheme(View button) {
        int id = button.getId();
        Intent intent = intent;

        if (id == R.id.dark_button) {
            Intent intent = new Intent(this, DarkActivity.class);

        } else if (id == R.id.light_dark_bar_button) {
            Intent intent = new Intent(this, light_Dark_bar.class);

        }
        else {
            Intent intent = new Intent(this, CustomActivity.class);

        }
        startActivity(intent);
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
        } else if (id == R.id.action_dark) {
            Intent intent = new Intent(this, DarkActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_light_Dark_bar) {

            Intent intent = new Intent(this, light_Dark_Bar.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
