package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {@ForeignKey(entity = Opponent.class,
        parentColumns = "parentClassColumn",
        childColumns = "childClassColumn",
        onDelete = ForeignKey.CASCADE)
})
public class Match {
    @PrimaryKey
    public int matchId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "result")
    public Object result; //TODO: change type

    public Match(int matchId, Date date, Object result) {
        this.matchId = matchId;
        this.date = date;
        this.result = result;
    }

    public Date getDate() {
        return date;
    }

    public int getMatchId() {
        return matchId;
    }

    public Object getResult() {
        return result;
    }
}
