package com.example.tennismatches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewOpponent extends AppCompatActivity {

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_opponent);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tennis-matches-db").build();
        OpponentDao opponentDao = db.opponentDao();

        executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        Button button = findViewById(R.id.add_opp);

        EditText firstName = findViewById(R.id.first_name_et);
        EditText secondName = findViewById(R.id.second_name_et);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Opponent opponent = new Opponent(15, firstName.getText().toString(), secondName.getText().toString());
                opponentDao.insertAll(opponent)
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DbInsertCompleteObserver());
            }
        });
    }

    private class DbInsertCompleteObserver implements CompletableObserver {
        DbInsertCompleteObserver() {
        }

        @Override
        public void onSubscribe(Disposable d) {
            Log.d("Insert opponent: ", "Trying to register an opponent");
        }

        @Override
        public void onComplete() {
            Toast.makeText(NewOpponent.this, "Avversario creato!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(NewOpponent.this, OpponentsPage.class);
            NewOpponent.this.startActivity(myIntent);
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Insert opponent: ", "Error -> " + e);
        }
    }
}