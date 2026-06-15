package com.prettyflights.model;

import com.prettyflights.model.enums.AircraftSize;
import java.time.LocalDateTime;
import java.util.*;

public class Gate {
    private String id;
    private String name;
    private AircraftSize maxAircraftSize;
    private int passengerFlowCapacity;
    private List<TimeSlot> occupiedSlots;
    
    public Gate(String id, String name, AircraftSize maxAircraftSize, int passengerFlowCapacity) {
        this.id = id;
        this.name = name;
        this.maxAircraftSize = maxAircraftSize;
        this.passengerFlowCapacity = passengerFlowCapacity;
        this.occupiedSlots = new ArrayList<>();
    }
    
    public boolean isCompatibleWith(Aircraft aircraft) {
        // Comparar tamanhos: WIDE_BODY > NARROW_BODY > REGIONAL
        return aircraft.getSize().ordinal() <= this.maxAircraftSize.ordinal();
    }
    
    public boolean isTimeSlotAvailable(LocalDateTime start, LocalDateTime end) {
        for (TimeSlot slot : occupiedSlots) {
            if (slotsOverlap(slot, start, end)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean slotsOverlap(TimeSlot existing, LocalDateTime start, LocalDateTime end) {
        return !(end.isBefore(existing.getStart()) || start.isAfter(existing.getEnd()));
    }
    
    public boolean validatePassengerFlow(int passengerCount) {
        return passengerCount <= passengerFlowCapacity;
    }
    
    public void reserveTimeSlot(LocalDateTime start, LocalDateTime end, String flightId) {
        occupiedSlots.add(new TimeSlot(start, end, flightId));
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPassengerFlowCapacity() { return passengerFlowCapacity; }
    
    // Inner class para slots de horário
    public static class TimeSlot {
        private LocalDateTime start;
        private LocalDateTime end;
        private String flightId;
        
        public TimeSlot(LocalDateTime start, LocalDateTime end, String flightId) {
            this.start = start;
            this.end = end;
            this.flightId = flightId;
        }
        
        public LocalDateTime getStart() { return start; }
        public LocalDateTime getEnd() { return end; }
    }
}
