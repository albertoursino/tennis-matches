package com.example.tennismatches;

import static com.example.tennismatches.MainActivity.executorService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.google.gson.Gson;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MatchPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("lastFragment", "matchesList");
                startActivity(intent);
                finish();
            }
        });

        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tennis-matches-db")
                .build();
        MatchDao matchDao = database.matchDao();

        Match match = new Gson().fromJson(getIntent().getStringExtra("matchSelected"), Match.class);

        Button button = findViewById(R.id.delete_match_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchDao.delete(match)
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new deleteMatchObserver());
            }
        });

    }

    private class deleteMatchObserver implements CompletableObserver {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onComplete() {
            Toast.makeText(MatchPage.this, "Partita eliminata con successo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("lastFragment", "matchesList");
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable e) {

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("lastFragment", "matchesList");
        startActivity(intent);
        finish();
    }
}