package service;

import model.Passenger;
import model.Flight;
import model.User;

import java.io.*;
import java.util.*;

public class FileManager {
    
    public List<Flight.FlightWithSource> loadAllFlights() {
        List<Flight.FlightWithSource> flights = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(flightFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                flights.add(Flight.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading flight data.");
        }
        return flights;
    }

    private final String userFile = "user_data.txt";
    public void saveUser(User user) {
        try (FileWriter fw = new FileWriter(userFile, true)) {
            fw.write(user.toFileString() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving user data.");
        }
    }

    public boolean verifyUser(String email, String password){
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user.email.equals(email) && user.password.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data.");
        }
        return false;
    }
        
    public boolean isUserRegistered(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user.email.equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking user data.");
        }
        return false;
    }

    public void deleteUser(String email) {
        try {
            File inputFile = new File(userFile);
            File tempFile = new File("temp_user_data.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(email + ",")) {
                    writer.write(line + System.lineSeparator());
                }
            }

            writer.close();
            reader.close();

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            }

        } catch (IOException e) {
            System.out.println("Error deleting user.");
        }
    }

    private final String adminFile = "admin.txt";
    public boolean isAdmin(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(adminFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(email) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading admin data.");
        }
        return false;
    }



    private final String fileName = "passenger_data.txt";
    public void savePassenger(Passenger p) {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            fw.write(p.toString() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving passenger.");
        }
    }

    private final String flightFileName = "flight_data.txt";
    

    public void saveFlight(String source,Flight flight) {
        try (FileWriter fw = new FileWriter(flightFileName, true)) {
            fw.write(flight.toFileStringWithSource(source)+ "\n"); 
            // System.out.println("Flight saved to file: " + flight.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving flight.");
        }
    }


    public void cancelPassenger(int id) {
        File inputFile = new File(fileName);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("PassengerID: " + id)) {
                    found = true;
                    continue;
                }
                writer.write(line + "\n");
            }
            if (found) {
                System.out.println("Cancellation successful.");
            } else {
                System.out.println("Passenger ID not found.");
            }
        } catch (IOException e) {
            System.out.println("Error processing cancellation.");
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // public boolean cancelFlightById(String source, int flightId) {
    //     File inputFile = new File(flightFileName);
    //     File tempFile = new File("temp_flight_data.txt");
    //     boolean found = false;

    //     try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    //         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             Flight.FlightWithSource fws = Flight.fromFileString(line);
    //             if (!(fws.source.equalsIgnoreCase(source) && fws.flight.id == flightId)) {
    //                 writer.write(line + System.lineSeparator());
    //             } else {
    //                 found = true;
    //             }
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Error canceling flight.");
    //         return false;
    //     }

    //     inputFile.delete();
    //     tempFile.renameTo(inputFile);
    //     return found;
    // }
    
}
