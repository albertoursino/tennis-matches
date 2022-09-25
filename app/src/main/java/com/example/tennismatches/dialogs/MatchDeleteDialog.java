package com.example.tennismatches.dialogs;

import static com.example.tennismatches.MainActivity.executorService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.tennismatches.MatchPage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MatchDeleteDialog extends DialogFragment {

    MatchDao matchDao;
    Match match;
    MatchPage.DeleteMatchObserver deleteMatchObserver;
    String message;

    public MatchDeleteDialog(MatchDao matchDao, Match match, MatchPage.DeleteMatchObserver deleteMatchObserver, String message) {
        this.matchDao = matchDao;
        this.match = match;
        this.deleteMatchObserver = deleteMatchObserver;
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        matchDao.delete(match)
                                .subscribeOn(Schedulers.from(executorService))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(deleteMatchObserver);
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
