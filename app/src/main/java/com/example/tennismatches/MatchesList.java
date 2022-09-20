package com.example.tennismatches;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MatchesList extends AppCompatActivity {

    List<String[]> tableData = new ArrayList<>();
    private final String[] TABLE_HEADERS = {"ID avversario", "Risultato", "Data"};
    TableView<String[]> tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_matches_list);

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

        tableView = findViewById(R.id.matches_table);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(getApplicationContext(), 3);
        columnModel.setColumnWidth(0, 100);
        columnModel.setColumnWidth(1, 150);
        columnModel.setColumnWidth(2, 150);
        tableView.setColumnModel(columnModel);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tennis-matches-db").build();
        MatchDao matchDao = db.matchDao();

        ExecutorService executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        matchDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DbGetCompleteObserver(getApplicationContext()));
    }

    private class DbGetCompleteObserver implements FlowableSubscriber<List<Match>> {

        Context context;

        DbGetCompleteObserver(Context context) {
            this.context = context;
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<Match> matches) {
            Calendar c = Calendar.getInstance();
            for (int i = 0; i < matches.size(); i++) {
                Date date = matches.get(i).getDate();
                c.setTime(date);
                tableData.add(new String[]{
                        matches.get(i).getOpponentId() + "",
                        matches.get(i).getResult(),
                        c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.YEAR)
                });
            }
            tableView.setDataAdapter(new SimpleTableDataAdapter(context, tableData));
        }

        @Override
        public void onError(Throwable t) {
            Log.d("MatchesList", "" + t);
        }

        @Override
        public void onComplete() {
        }
    }
}