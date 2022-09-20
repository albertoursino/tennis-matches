package com.example.tennismatches;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Random;

public class Utils {

    /**
     * @param type can be OPP or MAT
     * @return string id that 99.9% has never been created in the database
     */
    public static String createUniqueId(String type) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Random rand = new Random();
        int int_random = rand.nextInt(1000);
        return type + "-" + timestamp.getTime() + "-" + int_random;
    }
}
