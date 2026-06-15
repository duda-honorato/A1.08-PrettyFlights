package com.prettyflights.model;

import java.time.LocalDateTime;

public class Flight {
    private String id;
    private String flightNumber;
    private Aircraft aircraft;
    private int passengerCount;
    private LocalDateTime landingTime;
    private LocalDateTime takeoffTime;
    private String airline;
    private String allocatedGateId;
    
    public Flight(String id, String flightNumber, Aircraft aircraft, int passengerCount,
                  LocalDateTime landingTime, LocalDateTime takeoffTime, String airline) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.aircraft = aircraft;
        this.passengerCount = passengerCount;
        this.landingTime = landingTime;
        this.takeoffTime = takeoffTime;
        this.airline = airline;
    }
    
    public String getId() { return id; }
    public Aircraft getAircraft() { return aircraft; }
    public int getPassengerCount() { return passengerCount; }
    public LocalDateTime getLandingTime() { return landingTime; }
    public LocalDateTime getTakeoffTime() { return takeoffTime; }
    public void setAllocatedGateId(String gateId) { this.allocatedGateId = gateId; }
    public String getAllocatedGateId() { return allocatedGateId; }
}
