package service;

import model.Passenger;
import model.Flight;
import model.Graph;
import model.User;


import java.util.*;

public class BookingSystem{
    private Graph graph = new Graph();
    private Scanner sc = new Scanner(System.in);
    private FileManager fileManager = new FileManager();
    private int passengerIdCounter = 100;
    private int flightIdCounter = 1;
    private User currentUser = null;

    public BookingSystem() {
        loadFlightsFromFile();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Welcome to Airline Booking System ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Logout & Delete Account");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> {
                    if (loginUser()) {
                        showFlightMenu();
                    }
                }
                case 3 -> {
                    System.out.print("Enter your email to confirm logout & delete account: ");
                    String email = sc.nextLine();
                    fileManager.deleteUser(email);
                    System.out.println("Account deleted. Logged out successfully.");
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void registerUser() {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        if (fileManager.isUserRegistered(email)) {
            System.out.println("User already registered.");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        fileManager.saveUser(new User(email, password));
        System.out.println("Registration successful!");
    }

    private boolean loginUser() {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        if (fileManager.isAdmin(email, password)) {
            currentUser = new User(email, password, true);
            System.out.println("Admin login successful!");
            return true;
        } else if (fileManager.verifyUser(email, password)) {
            currentUser = new User(email, password, false);
            System.out.println("Passenger login successful!");
            return true;
        } else {
            System.out.println("Invalid email or password.");
            return false;
        }
    }

    
    private void showFlightMenu() {
        while (true) {
            System.out.println("\n--- Airline Menu ---");
            if (currentUser.isAdmin) {
                System.out.println("1. Add Flight");
                System.out.println("2. Show Flights");
                System.out.println("3. Logout");

                System.out.print("Enter choice: ");
                int choiceA = sc.nextInt();
                sc.nextLine();

                switch (choiceA) {
                    case 1 -> addFlight();
                    case 2 -> showFlights();
                    case 3 -> {
                        currentUser = null;
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
            else{
                System.out.println("1. Book Ticket");
                System.out.println("2. Cancel Ticket");
                System.out.println("3. Show Flights");
                System.out.println("4. Find Shortest Route");
                System.out.println("5. Find Flights in sorted order");
                System.out.println("6. Logout");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> bookTicket();
                    case 2 -> cancelTicket();
                    case 3 -> showFlights();
                    case 4 -> findShortestRoute();
                    case 5 -> showFlightsSortedByDuration();
                    case 6 -> {
                            currentUser = null;
                            System.out.println("Logging out...");
                            return;
                        }
                    default -> System.out.println("Invalid choice.");
                }
            }
            
            
        }
    }

    
    private void addFlight() {
        System.out.print("Enter source city: ");
        String source = sc.nextLine();
        System.out.print("Enter destination city: ");
        String destination = sc.nextLine();
        System.out.print("Enter price: ");
        int price = sc.nextInt();
        System.out.print("Enter duration (minutes): ");
        int duration = sc.nextInt();
        System.out.print("Enter available seats: ");
        int seats = sc.nextInt();
        sc.nextLine();

        Flight flight = new Flight(flightIdCounter++,destination, price, duration, seats);
        fileManager.saveFlight(source,flight);
        graph.addFlight(source, flight);
        System.out.println("Flight added!");
        // System.out.println("flight added to text file"); // Save flight 
    }
    private void bookTicket() {
        System.out.print("From city: ");
        String from = sc.nextLine().trim();
        System.out.print("To city: ");
        String to = sc.nextLine().trim();

        List<Flight> allFlights = graph.getFlightsFrom(from);
        List<Flight> matchingFlights = new ArrayList<>();

        for (Flight flight : allFlights) {
            if (flight.destination.equalsIgnoreCase(to)) {
                matchingFlights.add(flight);
            }
        }

        if (matchingFlights.isEmpty()) {
            System.out.println("No flights available between " + from + " and " + to);
            return;
        }

        System.out.println("\nAvailable Flights:");
        for (int i = 0; i < matchingFlights.size(); i++) {
            System.out.println(i + ": " + matchingFlights.get(i));
        }

        System.out.print("\nSelect flight index: ");
        int index = sc.nextInt();
        sc.nextLine(); // Clear newline

        if (index < 0 || index >= matchingFlights.size()) {
            System.out.println("Invalid flight selection.");
            return;
        }

        Flight selected = matchingFlights.get(index);

        System.out.print("Seats to book: ");
        int seats = sc.nextInt();
        sc.nextLine(); // Clear newline

        if (seats <= 0) {
            System.out.println("Seat count must be positive.");
            return;
        }

        if (selected.availableSeats < seats) {
            System.out.println("Not enough seats available. Only " + selected.availableSeats + " left.");
            return;
        }

        List<Passenger> passengersToSave = new ArrayList<>();

        for (int i = 0; i < seats; i++) {
            System.out.print("Enter name for passenger " + (i + 1) + ": ");
            String name = sc.nextLine().trim();

            Passenger p = new Passenger(currentUser.passengerId, name, from, to, selected.destination, 1);
            passengersToSave.add(p);
        }

        // Save all passengers
        for (Passenger p : passengersToSave) {
            fileManager.savePassenger(p);
        }

        // Deduct booked seats
        selected.availableSeats -= seats;

        System.out.println("Booking successful for " + seats + " seat(s).");
    }
    private void bookTicket(String from, String to, List<Flight> matchingFlights) {
        System.out.print("Seats to book: ");
        int seats = sc.nextInt();
        sc.nextLine(); // clear buffer

        if (seats <= 0) {
            System.out.println("Invalid number of seats.");
            return;
        }

        System.out.print("Select flight index: ");
        int index = sc.nextInt();
        sc.nextLine();

        if (index < 0 || index >= matchingFlights.size()) {
            System.out.println("Invalid flight selection.");
            return;
        }

        Flight selected = matchingFlights.get(index);
        if (selected.availableSeats < seats) {
            System.out.println("Not enough seats available.");
            return;
        }

        // Book each passenger
        for (int i = 0; i < seats; i++) {
            System.out.print("Enter name for passenger " + (i + 1) + ": ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Passenger name cannot be empty.");
                i--; // Retry this passenger
                continue;
            }

            selected.availableSeats--;
            Passenger p = new Passenger(currentUser.passengerId++, name, from, to, selected.destination, 1);
            fileManager.savePassenger(p);
        }

        System.out.println("Booking successful for " + seats + " passenger(s).");
    }


    // private void bookTicket() {
    //     System.out.print("Enter name: ");
    //     String name = sc.nextLine();
    //     System.out.print("From city: ");
    //     String from = sc.nextLine();
    //     System.out.print("To city: ");
    //     String to = sc.nextLine();

    //     List<Flight> allFlights = graph.getFlightsFrom(from);
    //     List<Flight> matchingFlights = new ArrayList<>();

    //     // Filter only those that match the destination
    //     for (Flight flight : allFlights) {
    //         if (flight.destination.equalsIgnoreCase(to)) {
    //             matchingFlights.add(flight);
    //         }
    //     }

    //     // If no matching flights found
    //     if (matchingFlights.isEmpty()) {
    //         System.out.println("No flights available between " + from + " and " + to);
    //         return;
    //     }

    //     // Show available flights
    //     for (int i = 0; i < matchingFlights.size(); i++) {
    //         System.out.println(i + ": " + matchingFlights.get(i));
    //     }

    //     System.out.print("Select flight index: ");
    //     int index = sc.nextInt();
    //     sc.nextLine(); // Clear newline buffer

    //     if (index < 0 || index >= matchingFlights.size()) {
    //         System.out.println("Invalid flight selection.");
    //         return;
    //     }

    //     System.out.print("Seats to book: ");
    //     int seats = sc.nextInt();
    //     sc.nextLine(); // Clear newline buffer

    //     Flight selected = matchingFlights.get(index);
    //     if (selected.availableSeats >= seats) {
    //         selected.availableSeats -= seats;
    //         Passenger p = new Passenger(currentUser.passengerId, name, from, to, selected.destination, seats);
    //         fileManager.savePassenger(p);
    //         System.out.println("Booking successful!");
    //     } else {
    //         System.out.println("Not enough seats available.");
    //     }
    // }


    private void cancelTicket() {
        System.out.print("Enter Passenger ID to cancel: ");
        int id = sc.nextInt();
        fileManager.cancelPassenger(id);
    }

    // private void showFlights() {
    //     System.out.print("Enter city: ");
    //     String city = sc.nextLine().trim().toLowerCase(); // Normalize input
    //     if (city.isEmpty()) {
    //         System.out.println("City name cannot be empty.");
    //         return;
    //     }

    //     List<Flight> flights = graph.getFlightsFrom(city);
    //     if (flights.isEmpty()) {
    //         System.out.println("No flights found from " + city + ".");
    //     } else {
    //         System.out.println("Flights from " + city + ":");
    //         for (Flight flight : flights) {
    //             System.out.println(flight);
    //         }
    //     }
    // }

   private void showFlights() {
        System.out.print("Enter source city: ");
        String source = sc.nextLine().trim().toLowerCase();

        System.out.print("Enter destination city: ");
        String destination = sc.nextLine().trim().toLowerCase();

        if (source.isEmpty() || destination.isEmpty()) {
            System.out.println("City names cannot be empty.");
            return;
        }

        List<Flight> flightsFromSource = graph.getFlightsFrom(source);
        List<Flight> matchingFlights = new ArrayList<>();

        for (Flight flight : flightsFromSource) {
            if (flight.destination.equalsIgnoreCase(destination)) {
                matchingFlights.add(flight);
            }
        }

        if (matchingFlights.isEmpty()) {
            System.out.println("No flights found from " + source + " to " + destination + ".");
        } else {
            System.out.println("Flights from " + source + " to " + destination + ":");
            for (int i = 0; i < matchingFlights.size(); i++) {
                System.out.println(i + ": " + matchingFlights.get(i));
            }

            System.out.print("Do you want to book from these flights? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                bookTicket(source, destination, matchingFlights);
            } else {
                System.out.println("Returning to main menu.");
            }
        }
    }
 

    private void findShortestRoute() {
        System.out.print("Enter source city: ");
        String source = sc.nextLine();
        System.out.print("Enter destination city: ");
        String dest = sc.nextLine();

        Graph.PathInfo path = graph.findShortestPathByDuration(source, dest);

        if (path == null) {
            System.out.println("No route found.");
            return;
        }

        System.out.println("Shortest route by duration:");
        for (int i = 0; i < path.pathFlights.size(); i++) {
            Flight f = path.pathFlights.get(i);
            System.out.println("FlightID: " + f.id + " | " + path.pathCities.get(i) + " -> " +
                            path.pathCities.get(i + 1) + " | Duration: " + f.duration + " mins");
        }
        // System.out.println("Total Duration: " + path.totalDuration + " mins");
    }

    private void showFlightsSortedByDuration() {
        System.out.print("Enter source city: ");
        String source = sc.nextLine().trim();
        System.out.print("Enter destination city: ");
        String destination = sc.nextLine().trim();

        List<Flight> flightsFromSource = graph.getFlightsFrom(source);

        // Filter flights that go to the desired destination
        List<Flight> matchingFlights = new ArrayList<>();
        for (Flight flight : flightsFromSource) {
            if (flight.destination.equalsIgnoreCase(destination)) {
                matchingFlights.add(flight);
            }
        }

        if (matchingFlights.isEmpty()) {
            System.out.println("No flights found from " + source + " to " + destination + ".");
            return;
        }

        // Sort by duration
        matchingFlights.sort(Comparator.comparingInt(f -> f.duration));

        // Print
        System.out.println("\nFlights from " + source + " to " + destination + " sorted by duration:");
        for (Flight flight : matchingFlights) {
            System.out.println("FlightID: " + flight.id +
                            " | Duration: " + flight.duration + " mins" +
                            " | Price: " + flight.price +
                            " | Seats: " + flight.availableSeats);
        }
    }

    private void loadFlightsFromFile() {
        List<Flight.FlightWithSource> flights = fileManager.loadAllFlights();
        for (Flight.FlightWithSource fws : flights) {
            graph.addFlight(fws.source, fws.flight);
        }
    }

}
