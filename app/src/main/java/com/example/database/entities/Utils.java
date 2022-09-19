package com.example.database.entities;

import static java.lang.Integer.valueOf;

import androidx.room.TypeConverter;

import java.util.Date;

public class Utils {
    public static class Converters {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }

        @TypeConverter
        public static Opponent fromString(String string) {
            String[] parts = string.split("/");
            return new Opponent(Integer.parseInt(parts[0]), parts[1], parts[2]);
        }

        @TypeConverter
        public static String opponentToString(Opponent opponent) {
            return opponent.getOppId() + "/" + opponent.getFirstName() + "/" + opponent.getLastName() + "/";
        }
    }
}
