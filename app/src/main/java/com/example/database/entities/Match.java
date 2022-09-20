package com.example.database.entities;

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
    public Date date;
    public String result;
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
