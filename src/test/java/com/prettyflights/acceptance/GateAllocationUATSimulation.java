package com.prettyflights.acceptance;

import com.prettyflights.model.*;
import com.prettyflights.model.enums.AircraftSize;
import com.prettyflights.repository.*;
import com.prettyflights.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GateAllocationUATSimulation {
    
    private GateRepository gateRepository;
    private GateAllocationService allocationService;
    private Map<String, Object> uiSimulation;
    
    @BeforeEach
    void setUp() {
        gateRepository = new GateRepository();
        AuditRepository auditRepository = new AuditRepository();
        GroundCrewService groundCrewService = new GroundCrewService();
        allocationService = new GateAllocationService(gateRepository, auditRepository, groundCrewService);
        
        // Setup do aeroporto para UAT
        gateRepository.save(new Gate("G001", "A10", AircraftSize.NARROW_BODY, 200));
        gateRepository.save(new Gate("G002", "B22", AircraftSize.WIDE_BODY, 350));
        gateRepository.save(new Gate("G003", "C05", AircraftSize.NARROW_BODY, 180));
        gateRepository.save(new Gate("G004", "D11", AircraftSize.WIDE_BODY, 400));
        gateRepository.save(new Gate("G005", "E03", AircraftSize.REGIONAL, 80));
        
        uiSimulation = new HashMap<>();
        uiSimulation.put("gateMap", new HashMap<String, String>());
    }
    
    @Test
    void testUAT_RealOperationalDay_ShouldMeetANACStandards() throws Exception {
        System.out.println("=== INÍCIO UAT - DIA OPERACIONAL REAL ===\n");
        
        // Cenário: 15 voos entre 10h e 14h
        List<Flight> flights = createFlightsForPeakHour();
        
        // Simular operação
        Map<String, List<Flight>> gateAssignments = new HashMap<>();
        int successfulAllocations = 0;
        int failedAllocations = 0;
        
        for (Flight flight : flights) {
            try {
                Gate gate = allocationService.allocateGate(flight);
                gateAssignments.computeIfAbsent(gate.getId(), k -> new ArrayList<>()).add(flight);
                successfulAllocations++;
                
                // Simular UI: atualizar mapa de portões
                @SuppressWarnings("unchecked")
                Map<String, String> gateMap = (Map<String, String>) uiSimulation.get("gateMap");
                gateMap.put(gate.getId(), String.format("FLIGHT %s (%s pax)", 
                    flight.getFlightNumber(), flight.getPassengerCount()));
                
                System.out.printf("✓ Voo %s alocado para %s (%s) - %d passageiros%n",
                    flight.getFlightNumber(), gate.getName(), gate.getId(), flight.getPassengerCount());
                
            } catch (Exception e) {
                failedAllocations++;
                System.err.printf("✗ Falha na alocação do voo %s: %s%n", 
                    flight.getFlightNumber(), e.getMessage());
            }
        }
        
        System.out.println("\n=== RESULTADOS DA OPERAÇÃO ===");
        System.out.println("Total voos processados: " + flights.size());
        System.out.println("Alocações bem-sucedidas: " + successfulAllocations);
        System.out.println("Falhas: " + failedAllocations);
        
        // Validações UAT
        // 1. Nenhum voo Wide Body alocado em portão Narrow
        for (Map.Entry<String, List<Flight>> entry : gateAssignments.entrySet()) {
            String gateId = entry.getKey();
            Gate gate = gateRepository.findById(gateId).get();
            
            for (Flight flight : entry.getValue()) {
                if (flight.getAircraft().getSize() == AircraftSize.WIDE_BODY) {
                    assertTrue(gate.getMaxAircraftSize() == AircraftSize.WIDE_BODY,
                        "Wide body flight " + flight.getFlightNumber
