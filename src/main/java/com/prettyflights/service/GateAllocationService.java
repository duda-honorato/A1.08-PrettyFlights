package com.prettyflights.service;

import com.prettyflights.model.*;
import com.prettyflights.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GateAllocationService {
    private GateRepository gateRepository;
    private AuditRepository auditRepository;
    private GroundCrewService groundCrewService;
    private Map<String, Flight> allocatedFlights;
    
    public GateAllocationService(GateRepository gateRepository, AuditRepository auditRepository,
                                  GroundCrewService groundCrewService) {
        this.gateRepository = gateRepository;
        this.auditRepository = auditRepository;
        this.groundCrewService = groundCrewService;
        this.allocatedFlights = new ConcurrentHashMap<>();
    }
    
    /**
     * Método principal de alocação - RF11
     */
    public Gate allocateGate(Flight flight) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 1. Buscar portões disponíveis
        List<Gate> allGates = gateRepository.findAll();
        
        // 2. Filtrar portões compatíveis
        List<Gate> compatibleGates = allGates.stream()
            .filter(gate -> gate.isCompatibleWith(flight.getAircraft()))
            .filter(gate -> gate.isTimeSlotAvailable(flight.getLandingTime(), flight.getTakeoffTime()))
            .filter(gate -> gate.validatePassengerFlow(flight.getPassengerCount()))
            .collect(Collectors.toList());
        
        if (compatibleGates.isEmpty()) {
            throw new Exception("No compatible gate available at requested time");
        }
        
        // 3. Escolher o melhor portão (critério: maior capacidade de fluxo)
        Gate selectedGate = compatibleGates.stream()
            .max(Comparator.comparingInt(Gate::getPassengerFlowCapacity))
            .orElseThrow(() -> new Exception("No suitable gate found"));
        
        // 4. Reservar o portão
        selectedGate.reserveTimeSlot(flight.getLandingTime(), flight.getTakeoffTime(), flight.getId());
        flight.setAllocatedGateId(selectedGate.getId());
        allocatedFlights.put(flight.getId(), flight);
        
        // 5. Registrar no banco de dados
        gateRepository.update(selectedGate);
        
        // 6. Registrar auditoria
        auditRepository.logAction("GATE_ASSIGNED", 
            String.format("Flight %s assigned to gate %s", flight.getId(), selectedGate.getId()));
        
        // 7. Notificar equipes
        groundCrewService.notifyGroundCrew(selectedGate, flight);
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime > 2000) {
            System.err.println("Allocation time exceeded 2 seconds: " + elapsedTime + "ms");
        }
        
        return selectedGate;
    }
    
    public Optional<Gate> findGateByFlightId(String flightId) {
        Flight flight = allocatedFlights.get(flightId);
        if (flight != null && flight.getAllocatedGateId() != null) {
            return gateRepository.findById(flight.getAllocatedGateId());
        }
        return Optional.empty();
    }
}
