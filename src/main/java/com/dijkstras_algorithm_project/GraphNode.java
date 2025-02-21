package com.dijkstras_algorithm_project;

import java.util.Arrays;

public class GraphNode {
    private int index; // Index in the table
    private Capital capital; // Capital object representing a city
    private LinkedList adjacencyList; // List of adjacent cities (routes)
    private boolean known;
    private Capital previous;
    private double[] weights;

    // Default Constructor
    public GraphNode() {
        this.adjacencyList = new LinkedList();
        this.weights = new double[]{0.0, 0.0, 0.0};
    }

    public GraphNode(Capital capital) {
        this.capital = capital;
        this.adjacencyList = new LinkedList();
        this.weights = new double[]{0.0, 0.0, 0.0};
    }

    // Constructor with index, Capital, and status
    public GraphNode(int index, Capital capital) {
        this.index = index;
        this.capital = capital;
        this.adjacencyList = new LinkedList();
        this.weights = new double[]{0.0, 0.0, 0.0};
    }

    public void setWeights(double[] weights) {
        this.weights[0] = weights[0]; // Distance
        this.weights[1] = weights[1]; // Cost
        this.weights[2] = weights[2]; // Time
    }

    // Getters and Setters
    public Capital getCapital() {
        return capital;
    }

    public void setCapital(Capital capital) {
        this.capital = capital;
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

    public void addRoute(FlightRoute route) {
        adjacencyList.add(route);
    }

    public void removeRoute(FlightRoute route) {
        adjacencyList.removeByTargetName(route.getTarget());
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public Capital getPrevious() {
        return previous;
    }

    public void setPrevious(Capital previous) {
        this.previous = previous;
    }

    public double[] getWeights() {
        return weights;
    }

    public double getTotalDistance() {
        return weights[0];
    }

    public double getTotalCost() {
        return weights[1];
    }

    public double getTotalTime() {
        return weights[2];
    }


    @Override
    public String toString() {
        return "GraphNode{" +
                "index=" + index +
                ", capital=" + capital +
                ", adjacencyList=" + adjacencyList +
                ", known=" + known +
                ", previous='" + previous + '\'' +
                ", totals=" + Arrays.toString(weights) +
                '}';
    }
}
