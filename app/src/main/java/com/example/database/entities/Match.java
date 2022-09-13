package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Match {
    @PrimaryKey
    public int matchId;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "result")
    public String result;
}
