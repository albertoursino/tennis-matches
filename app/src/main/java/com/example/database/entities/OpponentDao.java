package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface OpponentDao {
    @Query("SELECT * FROM opponent ORDER BY oppId ASC")
    Flowable<List<Opponent>> getAll();

    @Query("SELECT * FROM opponent WHERE firstName LIKE :first AND " +
            "lastName LIKE :last")
    Flowable<List<Opponent>> findByName(String first, String last);

    @Query("SELECT * FROM opponent WHERE oppId LIKE :oppId")
    Flowable<Opponent> findById(int oppId);

    @Query("SELECT Opponent.oppId, firstName, lastName FROM opponent " +
            "JOIN `match` ON opponent.oppId=`match`.oppId " +
            "WHERE matchId=:matchId;")
    Flowable<Opponent> findByMatchId(String matchId);

    @Insert
    Completable insertAll(Opponent... opponents);

    @Delete
    Completable delete(Opponent opponent);
}
