package com.example.database.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface OpponentDao {
    @Query("SELECT * FROM opponent")
    Flowable<List<Opponent>> getAll();

    @Query("SELECT * FROM opponent WHERE oppId IN (:oppIds)")
    Flowable<List<Opponent>> loadAllByIds(int[] oppIds);

    @Query("SELECT * FROM opponent WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    Flowable<Opponent> findByName(String first, String last);

    @Insert
    Completable insertAll(Opponent... opponents);

    @Delete
    Completable delete(Opponent opponent);
}
