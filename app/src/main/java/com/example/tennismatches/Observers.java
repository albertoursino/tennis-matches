package com.example.tennismatches;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class Observers {
    public static class DbInsertCompleteObserver implements CompletableObserver {

        Context context;
        String textError = "An error occurred";
        Class<?> activityToStart;

        DbInsertCompleteObserver(Context context, Class<?> activityToStart) {
            this.context = context;
            this.activityToStart = activityToStart;
        }

        DbInsertCompleteObserver(Context context, Class<?> activityToStart, String textError) {
            this.context = context;
            this.textError = textError;
            this.activityToStart = activityToStart;
        }

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onComplete() {
            Toast.makeText(context, "Avversario creato!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(context, OpponentsList.class);
            context.startActivity(myIntent);
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error: ", "" + e);
            if (e instanceof SQLiteConstraintException)
                Toast.makeText(context, textError, Toast.LENGTH_SHORT).show();
        }
    }
}
