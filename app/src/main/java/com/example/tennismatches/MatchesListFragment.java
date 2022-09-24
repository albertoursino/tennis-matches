package com.example.tennismatches;

import static com.example.tennismatches.MainActivity.executorService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
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
    AppDatabase db;
    OpponentDao opponentDao;
    MatchDao matchDao;
    TreeMap<String, Pair<Match, Opponent>> matchesOpponents = new TreeMap<>();
    List<Match> allMatches = new ArrayList<>();

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
        Context context = view.getContext();

        Button newMatchBtn = view.findViewById(R.id.new_match_btn);
        newMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, NewMatchForm.class);
                startActivity(myIntent);
            }
        });

        tableView = view.findViewById(R.id.matches_table);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(context, 1, 1000);
        tableView.setColumnModel(columnModel);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(context, TABLE_HEADERS));
        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                Match matchSelected = allMatches.get(allMatches.size() - 1 - rowIndex);
                Intent myIntent = new Intent(context, MatchPage.class);
                myIntent.putExtra("matchSelected", new Gson().toJson(matchSelected));
                startActivity(myIntent);
                getActivity().finish();
            }
        });

        db = Room.databaseBuilder(context,
                AppDatabase.class, "tennis-matches-db").build();
        matchDao = db.matchDao();
        opponentDao = db.opponentDao();

        matchDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new getMatchesObserver());

        // Inflate the layout for this fragment
        return view;
    }

    public void getOpponentsWithMatches() {
        for (int i = 0; i < allMatches.size(); i++) {
            String matchId = allMatches.get(i).getMatchId();
            opponentDao.findByMatchId(matchId)
                    .subscribeOn(Schedulers.from(executorService))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new getOpponentObserver(allMatches.get(i)));
        }
    }

    private class getMatchesObserver implements FlowableSubscriber<List<Match>> {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<Match> matches) {
            allMatches = matches;
            getOpponentsWithMatches();
        }

        @Override
        public void onError(Throwable t) {
            Log.d("MatchesListFragment", "" + t);
            Toast.makeText(getContext(), "Some error has occurred", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {
        }
    }

    private class getOpponentObserver implements FlowableSubscriber<Opponent> {

        Match match;

        getOpponentObserver(Match match) {
            this.match = match;
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Opponent opponent) {
            matchesOpponents.put(match.matchId, new Pair<>(match, opponent));
            if (matchesOpponents.size() == allMatches.size()) {
                populateMatchesTable();
            }
        }

        @Override
        public void onError(Throwable t) {
            Log.d("MatchesListFragment", "" + t);
            Toast.makeText(getContext(), "Some error has occurred", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {

        }
    }

    private void populateMatchesTable() {
        Iterator<Map.Entry<String, Pair<Match, Opponent>>> it = matchesOpponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Pair<Match, Opponent>> pair = (Map.Entry<String, Pair<Match, Opponent>>) it.next();
            it.remove(); // avoids a ConcurrentModificationException
            Calendar c = Calendar.getInstance();
            Date date = pair.getValue().first.getDate();
            c.setTime(date);
            tableData.add(new String[]{
                    "vs " + pair.getValue().second.getFirstName() + " " + pair.getValue().second.getLastName() + " il " +
                    c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
            });
        }
        Collections.reverse(tableData);
        tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), tableData));
    }
}