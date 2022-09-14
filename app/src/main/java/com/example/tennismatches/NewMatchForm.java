package com.example.tennismatches;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewMatchForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match_form);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tennis-matches-db").build();
        OpponentDao opponentDao = db.opponentDao();

        ExecutorService executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        Button button = findViewById(R.id.add_match);

        EditText date = findViewById(R.id.match_date);

        opponentDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DbGetCompleteObserver(getApplicationContext()));

        //TODO: EditText to Date format

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                //TODO: creation of a new match
                //Match match = new Match(sharedPref.getInt("id_match", -1), date, opponent);
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
            Toast.makeText(NewMatchForm.this, "Partita creata!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(NewMatchForm.this, MatchesList.class);
            startActivity(myIntent);
            // Update counter for the id of the matches (new match id = actual counter value)
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int counter = sharedPref.getInt("id_match", -1);
            if (counter != -1) {
                editor.putInt("id_match", ++counter);
                editor.apply();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error: ", "" + e);
        }
    }

    private class DbGetCompleteObserver implements FlowableSubscriber<List<Opponent>> {

        Context context;

        DbGetCompleteObserver(Context context) {
            this.context = context;
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<Opponent> opponents) {
            List<String> oppsName = new ArrayList<>();
            for (int i = 0; i < opponents.size(); i++) {
                oppsName.add("(" + opponents.get(i).getOppId() + ") " + opponents.get(i).getFirstName() + " " + opponents.get(i).getLastName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_spinner_item, oppsName);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.match_opponents_list);
            sItems.setAdapter(adapter);
        }

        @Override
        public void onError(Throwable t) {
            Log.d("Error: ", "" + t);
        }

        @Override
        public void onComplete() {
        }
    }
}