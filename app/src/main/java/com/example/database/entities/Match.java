package com.example.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {@ForeignKey(entity = Opponent.class,
        parentColumns = {"oppId"},
        childColumns = {"oppId"},
        onDelete = ForeignKey.CASCADE),
}, indices = {@Index(value = {"oppId"}, unique = true)})
public class Match {
    @PrimaryKey
    @NonNull
    public String matchId;

    public Date date;
    public String result;
    public String oppId;

    public Match(@NonNull String matchId, Date date, String result, String oppId) {
        this.matchId = matchId;
        this.date = date;
        this.result = result;
        this.oppId = oppId;
    }

    @NonNull
    public String getMatchId() {
        return matchId;
    }

    public Date getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getOpponentId() {
        return oppId;
    }
}
