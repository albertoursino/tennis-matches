package com.example.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Opponent {
    @PrimaryKey
    @NonNull
    public String oppId;

    // Entity properties
    public String firstName;
    public String lastName;
    public String notes;

    // Constructor
    public Opponent(@NonNull String oppId, String firstName, String lastName, String notes) {
        this.oppId = oppId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.notes = notes;
    }

    // Get methods
    @NonNull
    public String getOppId() {
        return oppId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNotes() {
        return notes;
    }
}
