package com.example.tennismatches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<String[]> tableData = new ArrayList<>();
    private final String[] TABLE_HEADERS = {"Le tue partite"};
    TableView<String[]> tableView;
    ExecutorService executorService;
    AppDatabase db;

    public MatchesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchesListFragment newInstance(String param1, String param2) {
        MatchesListFragment fragment = new MatchesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_matches_list, container, false);

        Button button = view.findViewById(R.id.new_match_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(view.getContext(), NewMatchForm.class);
                startActivity(myIntent);
            }
        });

        tableView = view.findViewById(R.id.matches_table);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(view.getContext(), 1, 1000);
        tableView.setColumnModel(columnModel);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(), TABLE_HEADERS));

        db = Room.databaseBuilder(view.getContext(),
                AppDatabase.class, "tennis-matches-db").build();
        MatchDao matchDao = db.matchDao();

        executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        matchDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DbGetCompleteObserver(view.getContext()));

        // Inflate the layout for this fragment
        return view;
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
                Match actualMatch = matches.get(i);
                Date date = actualMatch.getDate();
                c.setTime(date);
                tableData.add(new String[]{
                        " vs " + " " + " il " +
                                c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
                });
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