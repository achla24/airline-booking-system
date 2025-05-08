package model;

public class Flight {
    public int id;
    public String destination;
    public int price;
    public int duration;
    public int availableSeats;

    public Flight(int id, String destination, int price, int duration, int availableSeats) {
        this.id = id;
        this.destination = destination;
        this.price = price;
        this.duration = duration;
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "flight id: " + id + ", To: " + destination + ", Price: " + price + ", Duration: " + duration + ", Seats: " + availableSeats;
    }

    // Used to write flight details to file
    public String toFileString() {
        return id + "," + destination + "," + price + "," + duration + "," + availableSeats;
    }

    public String toFileStringWithSource(String source) {
    return id + "," + source + "," + destination + "," + price + "," + duration + "," + availableSeats;
    }


    // Helper class to hold source city and Flight object
    public static class FlightWithSource {
        public String source;
        public Flight flight;

        public FlightWithSource(String source, Flight flight) {
            this.source = source;
            this.flight = flight;
        }
    }

    // Used to read flight details from file
    public static FlightWithSource fromFileString(String data) {
        String[] parts = data.split(",");
        int id = Integer.parseInt(parts[0]);
        String source = parts[1];
        String destination = parts[2];
        int price = Integer.parseInt(parts[3]);
        int duration = Integer.parseInt(parts[4]);
        int availableSeats = Integer.parseInt(parts[5]);

        Flight flight = new Flight(id, destination, price, duration, availableSeats);
        return new FlightWithSource(source, flight);
    }
}
