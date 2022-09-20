package com.example.tennismatches;

import static com.example.tennismatches.Utils.createUniqueId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        Button addOppBtn = findViewById(R.id.add_opp);

        EditText firstName = findViewById(R.id.first_name_et);
        EditText secondName = findViewById(R.id.second_name_et);

        addOppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_opp = createUniqueId("OPP");
                Opponent opponent = new Opponent(id_opp, firstName.getText().toString(), secondName.getText().toString());
                opponentDao.insertAll(opponent)
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new insertOpponentObserver());
            }
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class insertOpponentObserver implements CompletableObserver {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onComplete() {
            Toast.makeText(NewOpponentForm.this, "Avversario creato!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(NewOpponentForm.this, MainActivity.class);
            myIntent.putExtra("lastFragment", "opponentsList");
            startActivity(myIntent);
            finish();
        }

        @Override
        public void onError(Throwable e) {
            Log.d("NewOpponentForm", "" + e);
        }
    }
}