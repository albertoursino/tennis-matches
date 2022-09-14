package com.example.tennismatches;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

public class NewOpponentForm extends AppCompatActivity {

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_opponent_form);

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
                Opponent opponent = new Opponent(firstName.getText().toString(), secondName.getText().toString());
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
        }

        @Override
        public void onComplete() {
            Toast.makeText(NewOpponentForm.this, "Avversario creato!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(NewOpponentForm.this, OpponentsList.class);
            startActivity(myIntent);
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error: ", "" + e);
            if (e instanceof SQLiteConstraintException)
                Toast.makeText(NewOpponentForm.this, "L'utente è già presente nel database", Toast.LENGTH_SHORT).show();
        }
    }
}