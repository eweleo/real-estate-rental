package com.example.application.utils;

public class Utils {

    static public String roomsLabel(int roomsNumber) {
        return roomsNumber == 1 ? " pokój" : roomsNumber < 5 ? " pokoje" : " pokojów";
    }

    static public String guestLabel(int maxPerson) {
        return maxPerson == 1 ? "1 osoba" : "do " + maxPerson + " osób";
    }

}
