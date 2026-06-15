package com.prettyflights.model;

import com.prettyflights.model.enums.AircraftSize;

public class Aircraft {
    private String id;
    private String model;
    private AircraftSize size;
    private int passengerCapacity;
    
    public Aircraft(String id, String model, AircraftSize size, int passengerCapacity) {
        this.id = id;
        this.model = model;
        this.size = size;
        this.passengerCapacity = passengerCapacity;
    }
    
    public String getId() { return id; }
    public AircraftSize getSize() { return size; }
    public int getPassengerCapacity() { return passengerCapacity; }
    
    // Getters e setters omitidos para brevidade
}
