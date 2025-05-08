package model;

public class User {
    public String email;
    public String password;
    public int passengerId;
    public boolean isAdmin = false; 

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.passengerId = generatePassengerId(email);
    }

    public User(String email, String password, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.passengerId = generatePassengerId(email);
    }

    private int generatePassengerId(String email) {
        return Math.abs(email.hashCode() % 10000) + 1000; // 1000–10999 range
    }

    public String toFileString() {
        return email + "," + password;
    }

    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        return new User(parts[0], parts[1]);
    }
}
