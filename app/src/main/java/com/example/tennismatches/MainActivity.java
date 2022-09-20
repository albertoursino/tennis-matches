package com.example.tennismatches;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    public static NavController navController;
    private long backPressed;
    private static final int TIME_INTERVAL_BACK_BTN = 2000;
    public static final ExecutorService executorService =
            new ThreadPoolExecutor(4, 5, 60,
                    TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null)
            navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        try {
            if (getIntent().getStringExtra("lastFragment").equals("opponentsList")) {
                navController.navigate(R.id.action_homeFragment_to_opponentsListFragment);
                bottomNavigationView.setSelectedItemId(R.id.opponents_bottom_btn);
            } else if(getIntent().getStringExtra("lastFragment").equals("matchesList")){
                navController.navigate(R.id.action_homeFragment_to_matchesListFragment);
                bottomNavigationView.setSelectedItemId(R.id.matches_bottom_btn);
            }
        } catch (NullPointerException e) {
            Log.d("MainActivity", "getIntent().getStringExtra(\"lastFragment\") null");
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_bottom_btn:
                    if (navController.getCurrentDestination().getId() == R.id.matchesListFragment)
                        navController.navigate(R.id.action_matchesListFragment_to_homeFragment);
                    else if (navController.getCurrentDestination().getId() == R.id.opponentsListFragment)
                        navController.navigate(R.id.action_opponentsListFragment_to_homeFragment);
                    break;
                case R.id.matches_bottom_btn:
                    if (navController.getCurrentDestination().getId() == R.id.opponentsListFragment)
                        navController.navigate(R.id.action_opponentsListFragment_to_matchesListFragment);
                    else if (navController.getCurrentDestination().getId() == R.id.homeFragment)
                        navController.navigate(R.id.action_homeFragment_to_matchesListFragment);
                    break;
                case R.id.opponents_bottom_btn:
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
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "Vai indietro di nuovo per uscire", Toast.LENGTH_SHORT);
        if (backPressed + TIME_INTERVAL_BACK_BTN > System.currentTimeMillis()) {
            finish();
        } else
            toast.show();
        backPressed = System.currentTimeMillis();
    }
}