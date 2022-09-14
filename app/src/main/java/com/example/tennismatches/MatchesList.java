package com.example.tennismatches;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MatchesList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches_list);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent myIntent = new Intent(MatchesList.this, MainActivity.class);
                MatchesList.this.startActivity(myIntent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button button = findViewById(R.id.new_match_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MatchesList.this, NewMatchForm.class);
                startActivity(myIntent);
            }
        });

    }
}