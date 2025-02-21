package com.dijkstras_algorithm_project;

import javafx.scene.control.TextField;

import java.io.IOException;

public class Graph {
    GraphNode[] table; // Array of (vertices)
    private static int cityCount = 0; // Tracks the current index for city insertion
    private static final double EARTH_RADIUS = 6371.0; // Radius of the earth in km

    // Constructor to initialize the graph with a set number of cities
    public Graph(int numCities) {
        table = new GraphNode[numCities];
    }

    // Add a city (Capital) to the graph
    public void addCapital(Capital capital) throws IOException {
        if (cityCount >= table.length) {
            System.out.println("Cannot add more cities. Graph is full.");
            return;
        }
        // Check if the capital already exists in the graph
        if (contains(capital)) {
            throw new IOException("Duplicate capital detected: " + capital.getName());
        }
        table[cityCount] = new GraphNode(cityCount, capital);
        capital.setIndex(cityCount);
        cityCount++;
    }

    // Add a Flight Route between two capitals
    public void addRoute(String source, String target, double cost, int time) {
        GraphNode sourceNode = findNodeByName(source);
        GraphNode targetNode = findNodeByName(target);

        if (sourceNode != null && targetNode != null) {
            sourceNode.addRoute(new FlightRoute(source, target, cost, time, getDistanceBetweenCapitals(sourceNode.getCapital(), targetNode.getCapital())));
        } else {
            if (sourceNode == null) {
                System.out.println("Source capital not found: " + source);
            } else {
                System.out.println("Target capital not found: " + target);
            }
        }
    }

    public boolean contains(Capital capital) {
        for (int i = 0; i < cityCount; i++) {
            if (table[i].getCapital().getName().equalsIgnoreCase(capital.getName())) {
                return true;
            }
        }
        return false;
    }

    public int find(Capital capital) {
        for (int i = 0; i < cityCount; i++) {
            if (table[i].getCapital().getName().equalsIgnoreCase(capital.getName())) {
                return i;
            }
        }
        return -1;
    }

    public GraphNode findNode(Capital capital) {
        for (int i = 0; i < cityCount; i++) {
            if (table[i].getCapital().getName().equalsIgnoreCase(capital.getName())) {
                return table[i];
            }
        }
        return null;
    }

    public GraphNode findNodeByName(String name) {
        for (int i = 0; i < cityCount; i++) {
            if (table[i].getCapital().getName().equalsIgnoreCase(name)) {
                return table[i];
            }
        }
        return null;
    }

    public boolean hasEdge(String source, String target) {
        return getEdge(source, target);
    }

    public boolean getEdge(String source, String target) {
        // Find the source capital in the graph
        GraphNode sourceNode = findNodeByName(source);

        if (sourceNode == null || sourceNode.getCapital() == null) {
            return false; // Source capital doesn't exist
        }

        // Get the adjacency list for the source capital
        LinkedList adjacencyList = sourceNode.getAdjacencyList();

        if (adjacencyList != null) {
            return adjacencyList.contains(target);
        }

        return false; // No matching edge found
    }

    public double getDistanceBetweenCapitals(Capital source, Capital target) {
        return calculateDistance(
                source.getLatitude(), source.getLongitude(),
                target.getLatitude(), target.getLongitude()
        );
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply the Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return EARTH_RADIUS * c;
    }

    // Display the graph (cities and their routes)
    public void displayGraph() {
        for (int i = 0; i < cityCount; i++) {
            System.out.println("City: " + table[i].getCapital().getName());
            table[i].getAdjacencyList().display();
        }
    }

    public String dijkstra(Capital source, Capital destination, int factorIndex, TextField costField, TextField timeField, TextField distanceField) {
        int sourceIndex = source.getIndex();

        // Initialize Table
        initializeTable(sourceIndex, factorIndex);

        // Main Algorithm Loop
        for (int count = 0; count < table.length; count++) {
            int vertex = getSmallestUnknownDistanceVertex(factorIndex);
            if (vertex == -1) break; // No more reachable vertices

            GraphNode currentNode = table[vertex];
            currentNode.setKnown(true); // Mark as known

            // Loop through adjacency list of vertex v
            LinkedList adjacencyList = currentNode.getAdjacencyList();
            Node node = adjacencyList.getFront();

            while (node != null) {
                FlightRoute route = (FlightRoute) node.getElement();
                int neighborIndex = findNodeByName(route.getTarget()).getIndex();

                if (neighborIndex != -1 && !table[neighborIndex].isKnown()) {
                    // Get the weight of the chosen factor
                    double weight = getRouteValueByIndex(route, factorIndex);
                    double newValue = currentNode.getWeights()[factorIndex] + weight;

                    if (newValue < table[neighborIndex].getWeights()[factorIndex]) {
                        // Update the selected factor (shortest path weight)
                        table[neighborIndex].getWeights()[factorIndex] = newValue;
                        table[neighborIndex].setPrevious(currentNode.getCapital());

                        // Update other totals based on the current route
                        for (int i = 0; i < 3; i++) {
                            if (i != factorIndex) { // Skip the selected factor; it's already updated
                                table[neighborIndex].getWeights()[i] = currentNode.getWeights()[i] + getRouteValueByIndex(route, i);
                            }
                        }
                    }
                }
                node = node.getNext();
            }
        }

        // Build the final path and set the fields
        return buildPath(source, destination, factorIndex, costField, timeField, distanceField);
    }

    private void initializeTable(int startIndex, int factorIndex) {
        for (int i = 0; i < table.length; i++) {
            GraphNode node = table[i];
            if (node != null) {
                node.setKnown(false);
                node.setPrevious(null);

                // Initialize only the selected factor to Double.MAX_VALUE
                node.getWeights()[factorIndex] = Double.MAX_VALUE;
            }
        }

        // Start vertex for the selected factor
        table[startIndex].getWeights()[factorIndex] = 0;
    }

    //Get the smallest unknown vertex index
    private int getSmallestUnknownDistanceVertex(int factorIndex) {
        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;

        // Iterate the table
        for (int i = 0; i < table.length; i++) {
            GraphNode node = table[i];
            if (node != null && !node.isKnown() && node.getWeights()[factorIndex] < minDistance) {
                minDistance = node.getWeights()[factorIndex];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private String buildPath(Capital source, Capital destination, int factor, TextField costField, TextField timeField, TextField distanceField) {
        StringBuilder path = new StringBuilder();
        Capital current = destination;

        if (destination.getIndex() == -1 || table[destination.getIndex()].getWeights()[(factor)] == Double.MAX_VALUE) {
            return "No valid path from " + source + " to " + destination;
        }

        // Fetch the totals from the destination node
        double totalDistance = table[destination.getIndex()].getWeights()[0];
        double totalCost = table[destination.getIndex()].getWeights()[1];
        double totalTime = table[destination.getIndex()].getWeights()[2];

        // Reconstruct the path
        while (current != null && !current.equals(source)) {
            int currentIndex = current.getIndex();
            if (currentIndex == -1) break;

            GraphNode entry = table[currentIndex];
            if (entry == null || entry.getPrevious() == null) break;

            path.insert(0, " --> " + current.getName());
            current = entry.getPrevious();
        }

        path.insert(0, source);

        // Set final totals in text fields
        distanceField.setText(String.format("%.2f km", totalDistance));
        costField.setText(String.format("%.2f $", totalCost));
        formatTime(totalTime, timeField);

        return String.format("Shortest path (%s): %s", factor, path);
    }


    private void formatTime(double totalTime, TextField timeField) {
        int totalMinutes = (int) totalTime;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String timeText;

        if (totalMinutes < 60) {
            // Less than an hour, display only minutes
            timeText = String.format("%02d minutes", minutes);
            timeField.setText(timeText);
        } else if (minutes == 0) {
            // Exact hours
            timeText = String.format("%02dh 00min", hours);
            timeField.setText(timeText);
        } else {
            // Hours and minutes
            timeText = String.format("%02dh %02dmin", hours, minutes);
            timeField.setText(timeText);
        }
    }

    private double getRouteValueByIndex(FlightRoute route, int index) {
        switch (index) {
            case 0: return route.getDistance(); // Distance
            case 1: return route.getCost();     // Cost
            case 2: return route.getTime();     // Time
            default:
                throw new IllegalArgumentException("Invalid index. Use 0 (distance), 1 (cost), or 2 (time).");
        }
    }
}