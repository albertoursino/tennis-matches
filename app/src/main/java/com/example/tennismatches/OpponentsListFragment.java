package com.example.tennismatches;


import static com.example.tennismatches.MainActivity.executorService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.database.AppDatabase;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

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
 * Use the {@link OpponentsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpponentsListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TableView tableView;
    List<String[]> tableData = new ArrayList<>();
    private static final String[] TABLE_HEADERS = {"I tuoi avversari"};
    List<Opponent> allOpponents = new ArrayList<>();
    Context context;

    public OpponentsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpponentsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpponentsListFragment newInstance(String param1, String param2) {
        OpponentsListFragment fragment = new OpponentsListFragment();
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

        View view = inflater.inflate(R.layout.fragment_opponents_list, container, false);
        context = view.getContext();

        tableView = view.findViewById(R.id.opponents_table);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(context, 1, 500);
        tableView.setColumnModel(columnModel);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(context, TABLE_HEADERS));
        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                Opponent opponentSelected = allOpponents.get(rowIndex);
                Intent myIntent = new Intent(context, OpponentPage.class);
                myIntent.putExtra("opponentSelected", new Gson().toJson(opponentSelected));
                startActivity(myIntent);
                getActivity().finish();
            }
        });

        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "tennis-matches-db").build();
        OpponentDao opponentDao = db.opponentDao();

        opponentDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new getAllOpponentsObserver());

        Button button = view.findViewById(R.id.new_opp_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, NewOpponentForm.class);
                startActivity(myIntent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private class getAllOpponentsObserver implements FlowableSubscriber<List<Opponent>> {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<Opponent> opponents) {
            allOpponents = opponents;
            for (int i = 0; i < opponents.size(); i++) {
                tableData.add(new String[]{opponents.get(i).getFirstName() + " " + opponents.get(i).getLastName()});
            }
            tableView.setDataAdapter(new SimpleTableDataAdapter(context, tableData));
        }

        @Override
        public void onError(Throwable t) {
            Log.d("OpponentsListFragment", "" + t);
        }

        @Override
        public void onComplete() {
        }
    }
}