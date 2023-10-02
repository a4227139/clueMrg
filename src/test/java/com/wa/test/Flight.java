package com.wa.test;

import java.io.Serializable;

public class Flight implements Serializable {

    private String flightId;
    private String destination;
    private double cost;

    public Flight(){

    }

    public Flight(String flightId, String destination) {
        this.flightId = flightId;
        this.destination = destination;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getDestination() {
        return destination;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "com.wa.test.Flight{" +
                "flightId='" + flightId + '\'' +
                ", destination='" + destination + '\'' +
                ", cost=" + cost +
                '}';
    }
}
