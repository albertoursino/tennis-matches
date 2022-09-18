package com.example.tennismatches;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    NavController navController;
    private long backPressed;
    private static final int TIME_INTERVAL_BACK_BTN = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null)
            navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_page:
                    if (navController.getCurrentDestination().getId() == R.id.matchesListFragment)
                        navController.navigate(R.id.action_matchesListFragment_to_homeFragment);
                    else if (navController.getCurrentDestination().getId() == R.id.opponentsListFragment)
                        navController.navigate(R.id.action_opponentsListFragment_to_homeFragment);
                    break;
                case R.id.matches_page:
                    if (navController.getCurrentDestination().getId() == R.id.opponentsListFragment)
                        navController.navigate(R.id.action_opponentsListFragment_to_matchesListFragment);
                    else if (navController.getCurrentDestination().getId() == R.id.homeFragment)
                        navController.navigate(R.id.action_homeFragment_to_matchesListFragment);
                    break;
                case R.id.opponents_page:
                    if (navController.getCurrentDestination().getId() == R.id.homeFragment)
                        navController.navigate(R.id.action_homeFragment_to_opponentsListFragment);
                    else if (navController.getCurrentDestination().getId() == R.id.matchesListFragment)
                        navController.navigate(R.id.action_matchesListFragment_to_opponentsListFragment);
                    break;
            }
            return true;
        });
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
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "Vai indietro di nuovo per uscire", Toast.LENGTH_SHORT);
        if (backPressed + TIME_INTERVAL_BACK_BTN > System.currentTimeMillis()) {
            finish();
        } else
            toast.show();
        backPressed = System.currentTimeMillis();
    }
}