package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {@ForeignKey(entity = Opponent.class,
        parentColumns = {"oppId"},
        childColumns = {"matchId"},
        onDelete = ForeignKey.CASCADE)
})
public class Match {
    @PrimaryKey
    public int matchId;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "result")
    public String result; //TODO: change type

    public Match(int matchId, String date, String result) {
        this.matchId = matchId;
        this.date = date;
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public int getMatchId() {
        return matchId;
    }

    public String getResult() {
        return result;
    }
}
