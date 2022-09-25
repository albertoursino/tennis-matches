package com.example.tennismatches;

import static com.example.tennismatches.MainActivity.executorService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.example.tennismatches.dialogs.MatchDeleteDialog;
import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MatchPage extends AppCompatActivity {

    AppDatabase database;
    MatchDao matchDao;
    OpponentDao opponentDao;
    Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_page);

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tennis-matches-db").build();
        matchDao = database.matchDao();
        opponentDao = database.opponentDao();

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

        match = new Gson().fromJson(getIntent().getStringExtra("matchSelected"), Match.class);

        // Populating fields
        ((TextView) findViewById(R.id.result_tw)).setText(match.getResult());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        String strDate = dateFormat.format(match.getDate());
        ((TextView) findViewById(R.id.date_tw)).setText(strDate);

        opponentDao.findByMatchId(match.getMatchId())
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new getOpponentByMatchId());

        findViewById(R.id.delete_match_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteMatchObserver deleteMatchObserver = new DeleteMatchObserver();
                DialogFragment newFragment = new MatchDeleteDialog(matchDao, match, deleteMatchObserver, "Sei sicuro di voler eliminare la partita?");
                newFragment.show(getSupportFragmentManager(), "delete_match");
            }
        });
    }

    public class DeleteMatchObserver implements CompletableObserver {
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

    private class getOpponentByMatchId implements FlowableSubscriber<Opponent> {

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Opponent opp) {
            ((TextView) findViewById(R.id.opp_tw)).setText(opp.getFirstName() + " " + opp.getLastName());
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onComplete() {

        }
    }
}