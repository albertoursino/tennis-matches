package com.example.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity
public class Opponent {
    @PrimaryKey
    public int oppId;
    // Entity properties
    public String firstName;
    public String lastName;

    // Constructor
    public Opponent(int oppId, String firstName, String lastName) {
        this.oppId = oppId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Get methods
    public int getOppId() {
        return oppId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
