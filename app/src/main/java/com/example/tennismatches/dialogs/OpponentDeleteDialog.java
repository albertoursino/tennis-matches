package com.example.tennismatches.dialogs;

import static com.example.tennismatches.MainActivity.executorService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;
import com.example.tennismatches.OpponentPage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OpponentDeleteDialog extends DialogFragment {

    OpponentDao opponentDao;
    Opponent opponent;
    OpponentPage.DeleteOpponentObserver deleteOpponentObserver;
    String message;

    public OpponentDeleteDialog(OpponentDao opponentDao, Opponent opponent, OpponentPage.DeleteOpponentObserver deleteOpponentObserver, String message) {
        this.opponentDao = opponentDao;
        this.opponent = opponent;
        this.deleteOpponentObserver = deleteOpponentObserver;
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        opponentDao.delete(opponent)
                                .subscribeOn(Schedulers.from(executorService))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(deleteOpponentObserver);
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

