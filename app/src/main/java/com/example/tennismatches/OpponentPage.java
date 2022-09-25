package com.example.tennismatches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.example.database.AppDatabase;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.example.tennismatches.dialogs.OpponentDeleteDialog;
import com.google.gson.Gson;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

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

        findViewById(R.id.delete_opp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteOpponentObserver deleteOpponentObserver = new DeleteOpponentObserver();
                DialogFragment newFragment = new OpponentDeleteDialog(opponentDao, opponent, deleteOpponentObserver,
                        "Sei sicuro di voler eliminare " + opponent.getFirstName() + " " + opponent.getLastName() + "?");
                newFragment.show(getSupportFragmentManager(), "delete_match");
            }
        });

        // Populating fields
        ((TextView) findViewById(R.id.name_tw)).setText(opponent.getFirstName());
        ((TextView) findViewById(R.id.surname_tw)).setText(opponent.getLastName());
        ((TextView) findViewById(R.id.notes_tw)).setText(opponent.getNotes());
    }

    public class DeleteOpponentObserver implements CompletableObserver {

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