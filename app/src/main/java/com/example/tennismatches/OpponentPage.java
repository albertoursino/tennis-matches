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
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.google.gson.Gson;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class OpponentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent_page);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("lastFragment", "opponentsList");
                startActivity(intent);
                finish();
            }
        });

        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tennis-matches-db")
                .build();
        OpponentDao opponentDao = database.opponentDao();

        Opponent opponent = new Gson().fromJson(getIntent().getStringExtra("opponentSelected"), Opponent.class);

        Button button = findViewById(R.id.delete_opp_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opponentDao.delete(opponent)
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new deleteOpponentObserver());
            }
        });

    }

    private class deleteOpponentObserver implements CompletableObserver {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onComplete() {
            Toast.makeText(OpponentPage.this, "Avversario eliminato con successo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("lastFragment", "opponentsList");
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
        intent.putExtra("lastFragment", "opponentsList");
        startActivity(intent);
        finish();
    }
}