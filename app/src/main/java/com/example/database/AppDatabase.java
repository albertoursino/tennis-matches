package com.example.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.database.entities.Match;
import com.example.database.entities.MatchDao;
import com.example.database.entities.Opponent;
import com.example.database.entities.OpponentDao;

@Database(entities = {Match.class, Opponent.class},
        version = 1
//        autoMigrations = {
//                @AutoMigration(from = 1, to = 2)
//        }
        )
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract OpponentDao opponentDao();

    public abstract MatchDao matchDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "tennis-matches-db.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
