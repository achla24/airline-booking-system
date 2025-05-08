package model;

public class Passenger {
    public int id;
    public String name;
    public String from;
    public String to;
    public String flightName;
    public int seats;

    public Passenger(int id, String name, String from, String to, String flightName, int seats) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.flightName = flightName;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "PassengerID: " + id + " | Name: " + name + " | From: " + from + " | To: " + to +
               " | Flight: " + flightName + " | Seats: " + seats;
    }

    
}
