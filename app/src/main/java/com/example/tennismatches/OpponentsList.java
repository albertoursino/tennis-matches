package com.example.tennismatches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.database.AppDatabase;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
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

public class OpponentsList extends AppCompatActivity {

    List<String[]> tableData = new ArrayList<>();
    private final String[] TABLE_HEADERS = {"Nome", "Cognome", "# partite"};
    TableView<String[]> tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponents_list);

        tableView = findViewById(R.id.matchTable);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(getApplicationContext(), 3);
        columnModel.setColumnWidth(0, 150);
        columnModel.setColumnWidth(1, 150);
        columnModel.setColumnWidth(2, 150);
        tableView.setColumnModel(columnModel);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tennis-matches-db").build();
        OpponentDao opponentDao = db.opponentDao();

        ExecutorService executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        opponentDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DbGetCompleteObserver(getApplicationContext()));

        Button button = findViewById(R.id.new_opp_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(OpponentsList.this, NewOpponentForm.class);
                OpponentsList.this.startActivity(myIntent);
            }
        });
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
            for (int i = 0; i < opponents.size(); i++) {
                tableData.add(new String[]{opponents.get(i).getFirstName(),
                        opponents.get(i).getLastName()});
            }
            tableView.setDataAdapter(new SimpleTableDataAdapter(context, tableData));
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