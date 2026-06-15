
package com.prettyflights.service;

import com.prettyflights.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroundCrewService {
    private Queue<String> groundTasks;
    
    public GroundCrewService() {
        this.groundTasks = new ConcurrentLinkedQueue<>();
    }
    
    public void notifyGroundCrew(Gate gate, Flight flight) {
        String task = String.format("Prepare Gate %s for flight %s (Landing: %s, Takeoff: %s)",
            gate.getName(), flight.getFlightNumber(), 
            flight.getLandingTime(), flight.getTakeoffTime());
        groundTasks.add(task);
        
        // Simular disparo de evento para mensageria
        System.out.println("[EVENT] GateAllocatedEvent: " + 
            String.format("gateId=%s, flightId=%s, estimatedBoardingTime=%s, groundCrewRequired=true",
                gate.getId(), flight.getId(), flight.getLandingTime()));
    }
    
    public List<String> getPendingTasks() {
        return new ArrayList<>(groundTasks);
    }
}
