package model;

import java.util.*;

public class Graph {
    public Map<String, List<Flight>> routes = new HashMap<>();

    public void addFlight(String source, Flight flight) {
        source = source.toLowerCase();
        routes.computeIfAbsent(source, k -> new ArrayList<>()).add(flight);
        // if (!routes.containsKey(source)) {
        //     routes.put(source, new ArrayList<>());
        // }
        // routes.get(source).add(flight);
    }

    public List<Flight> getFlightsFrom(String city) {
        city = city.toLowerCase();
        return routes.getOrDefault(city, new ArrayList<>());
    }

    public class PathInfo {
        public int totalDuration;
        public List<String> pathCities = new ArrayList<>();
        public List<Flight> pathFlights = new ArrayList<>();
    }

    public PathInfo findShortestPathByDuration(String source, String destination) {
        class Node implements Comparable<Node> {
            String city;
            int duration;
            List<String> path;
            List<Flight> flights;

            Node(String city, int duration, List<String> path, List<Flight> flights) {
                this.city = city;
                this.duration = duration;
                this.path = new ArrayList<>(path);
                this.flights = new ArrayList<>(flights);
            }

            public int compareTo(Node other) {
                return Integer.compare(this.duration, other.duration);
            }
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        pq.add(new Node(source, 0, List.of(source), new ArrayList<>()));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.city)) continue;
            visited.add(current.city);

            if (current.city.equals(destination)) {
                PathInfo info = new PathInfo();
                info.totalDuration = current.duration;
                info.pathCities = current.path;
                info.pathFlights = current.flights;
                return info;
            }

            for (Flight flight : getFlightsFrom(current.city)) {
                if (!visited.contains(flight.destination)) {
                    List<String> newPath = new ArrayList<>(current.path);
                    newPath.add(flight.destination);
                    List<Flight> newFlights = new ArrayList<>(current.flights);
                    newFlights.add(flight);
                    pq.add(new Node(flight.destination, current.duration + flight.duration, newPath, newFlights));
                }
            }
        }

        return null;
    }
}

