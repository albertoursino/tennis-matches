package com.example.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"firstName", "lastName"})
public class Opponent {
    // Entity properties
    @NonNull
    public String firstName;
    @NonNull
    public String lastName;

    // Constructor
    public Opponent(@NonNull String firstName, @NonNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Get methods
    @NonNull
    public String getFirstName() {
        return firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }
}
