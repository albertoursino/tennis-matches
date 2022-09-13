package com.example.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Opponent {
    // Entity properties
    @PrimaryKey
    public int oppId;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    // Constructor
    public Opponent(int oppId, String firstName, String lastName) {
        this.oppId = oppId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Get methods
    public int getId() {
        return oppId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
