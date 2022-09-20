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

    // Constructor
    public Opponent(@NonNull String oppId, String firstName, String lastName) {
        this.oppId = oppId;
        this.firstName = firstName;
        this.lastName = lastName;
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
}
