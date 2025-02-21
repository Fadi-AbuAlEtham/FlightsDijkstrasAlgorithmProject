package com.dijkstras_algorithm_project;

public class Capital {
    private int index;
    private String name;
    private double latitude;
    private double longitude;
    private LinkedList adjacencyList;

    // Default Constructor
    public Capital() {
        this.adjacencyList = new LinkedList();
    }

    // Constructor with Name, Latitude, Longitude
    public Capital(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adjacencyList = new LinkedList();
    }

    // Constructor with Name, Latitude, Longitude, and Adjacency List (rarely needed)
    public Capital(String name, double latitude, double longitude, LinkedList adjacencyList) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adjacencyList = adjacencyList != null ? adjacencyList : new LinkedList();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LinkedList getAdjacencyList() {
        return adjacencyList;
    }
    public void setAdjacencyList(LinkedList adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // Display Routes for This Capital
    public void displayRoutes() {
        System.out.println("Routes from " + name + ":");
        adjacencyList.display();
    }

    @Override
    public String toString() {
        return name;
    }

    public String displayCapital() {
        return "Capital{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
