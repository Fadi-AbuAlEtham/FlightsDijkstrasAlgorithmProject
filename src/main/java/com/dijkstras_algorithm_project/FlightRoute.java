package com.dijkstras_algorithm_project;

//  Represents a flight connection between two capitals (vertices) in the graph.
public class FlightRoute {
    private String source; // Source capital
    private String target; // Target capital
    private double cost;    // Flight cost in USD
    private int time;       // Flight time in minutes
    private double distance;

    // Default Constructor
    public FlightRoute() {
    }

    public FlightRoute(String source, String target, double cost, int time) {
        this.source = source;
        this.target = target;
        this.cost = cost;
        this.time = time;
        distance = 0;
    }

    public FlightRoute(String source, String target, double cost, int time, double distance) {
        this.source = source;
        this.target = target;
        this.cost = cost;
        this.time = time;
        this.distance = distance;
    }

    // Getters and Setters

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    // Display Method for Debugging
    public void display() {
        System.out.println(
                "Flight Route: " + source + " → " + target +
                        " | Cost: $" + cost +
                        " | Time: " + time + " min" +
                        " | Distance: " + distance + " km/h"
        );
    }

    @Override
    public String toString() {
        return "FlightRoute{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", cost=" + cost +
                ", time=" + time +
                ", distance=" + distance +
                '}';
    }
}