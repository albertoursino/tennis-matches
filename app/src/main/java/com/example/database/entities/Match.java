package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {@ForeignKey(entity = Opponent.class,
        parentColumns = {"oppId"},
        childColumns = {"oppId"},
        onDelete = ForeignKey.CASCADE)
})
public class Match {
    @PrimaryKey
    public int matchId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "result")
    public String result;

    @ColumnInfo(name = "oppId")
    public int oppId;

    public Match(int matchId, Date date, String result, int oppId) {
        this.matchId = matchId;
        this.date = date;
        this.result = result;
        this.oppId = oppId;
    }

    public int getMatchId() {
        return matchId;
    }

    public Date getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public int getOpponentId() {
        return oppId;
    }
}
