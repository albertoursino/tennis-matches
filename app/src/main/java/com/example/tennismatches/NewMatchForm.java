package com.example.tennismatches;

import static com.example.tennismatches.MainActivity.executorService;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.database.AppDatabase;
import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

import org.reactivestreams.Subscription;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewMatchForm extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Button datePickerButton;
    Date matchDate;
    List<Opponent> allOpponents;
    Opponent selectedOpp;
    int oppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match_form);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tennis-matches-db").build();
        OpponentDao opponentDao = db.opponentDao();
        MatchDao matchDao = db.matchDao();

        opponentDao.getAll()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new getAllOpponentsObserver());

        // Result
        EditText result = findViewById(R.id.result);
        Button buttonErase = findViewById(R.id.erase_result_btn);

        buttonErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
            }
        });

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = result.getText().toString();
                if (str.length() >= 1) {
                    boolean matches = str.substring(str.length() - 1).matches("\\d");
                    if (str.length() >= 3) {
                        if (str.substring(str.length() - 3).matches("\\d-\\d")) {
                            result.append(" ");
                        } else if (matches) {
                            result.append("-");
                        }
                    } else if (matches) {
                        result.append("-");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Date field
        initDatePicker();
        datePickerButton = findViewById(R.id.match_date);
        datePickerButton.setText(getTodayDate());
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        // Add match button
        Spinner spinner = (Spinner) findViewById(R.id.opponents_list_spinner);
        Button addMatchBtn = findViewById(R.id.add_match_btn);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                oppId = (int) l;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        addMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                int idMatch = sharedPref.getInt("id_match", 0);
                String res = result.getText().toString();
                try {
                    String s = datePickerButton.getText().toString();
                    matchDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Match match = new Match(idMatch, matchDate, res, oppId);
                matchDao.insertAll(match)
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new insertMatchObserver());
            }
        });

        // Back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "/" + month + "/" + year;
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String dateStr = day + "/" + month + "/" + year;
                datePickerButton.setText(dateStr);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    // Observers
    private class insertMatchObserver implements CompletableObserver {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onComplete() {
            Toast.makeText(NewMatchForm.this, "Partita creata!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(NewMatchForm.this, MainActivity.class);
            myIntent.putExtra("lastFragment", "matchesList");
            startActivity(myIntent);
            // Update counter for the id of the matches (new match id = actual counter value)
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int counter = sharedPref.getInt("id_match", 0);
            editor.putInt("id_match", ++counter);
            editor.apply();
            finish();
        }

        @Override
        public void onError(Throwable e) {
            Log.d("NewMatchForm", "" + e);
        }
    }

    private class getAllOpponentsObserver implements FlowableSubscriber<List<Opponent>> {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<Opponent> opponents) {
            allOpponents = opponents;
            List<String> oppsName = new ArrayList<>();
            for (int i = 0; i < opponents.size(); i++) {
                oppsName.add(opponents.get(i).getFirstName() + " " + opponents.get(i).getLastName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getApplicationContext(), android.R.layout.simple_spinner_item, oppsName);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.opponents_list_spinner);
            sItems.setAdapter(adapter);
        }

        @Override
        public void onError(Throwable t) {
            Log.d("NewMatchForm", "" + t);
        }

        @Override
        public void onComplete() {
        }
    }
}