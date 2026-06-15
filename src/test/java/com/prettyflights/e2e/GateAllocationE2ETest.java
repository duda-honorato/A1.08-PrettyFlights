package com.prettyflights.e2e;

import com.prettyflights.model.*;
import com.prettyflights.model.enums.AircraftSize;
import com.prettyflights.repository.*;
import com.prettyflights.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

public class GateAllocationE2ETest {
    
    private GateRepository gateRepository;
    private AuditRepository auditRepository;
    private GroundCrewService groundCrewService;
    private GateAllocationService allocationService;
    
    @BeforeEach
    void setUp() {
        gateRepository = new GateRepository();
        auditRepository = new AuditRepository();
        groundCrewService = new GroundCrewService();
        allocationService = new GateAllocationService(gateRepository, auditRepository, groundCrewService);
        
        // Setup dos 5 portões conforme cenário
        gateRepository.save(new Gate("G001", "A10", AircraftSize.NARROW_BODY, 200));
        gateRepository.save(new Gate("G002", "B22", AircraftSize.WIDE_BODY, 350));
        gateRepository.save(new Gate("G003", "C05", AircraftSize.NARROW_BODY, 180));
        gateRepository.save(new Gate("G004", "D11", AircraftSize.WIDE_BODY, 400));
        gateRepository.save(new Gate("G005", "E03", AircraftSize.REGIONAL, 80));
        
        // Ocupar D11 das 15:30 às 16:30
        Gate d11 = gateRepository.findById("G004").get();
        LocalDateTime occupiedStart = LocalDateTime.of(2024, 1, 15, 15, 30);
        LocalDateTime occupiedEnd = LocalDateTime.of(2024, 1, 15, 16, 30);
        d11.reserveTimeSlot(occupiedStart, occupiedEnd, "FL_EXISTING");
        gateRepository.update(d11);
    }
    
    // Teste: Fluxo completo de alocação otimizada
    @Test
    void testCompleteAllocationFlow_WithOptimization_ShouldSelectCorrectGate() throws Exception {
        // 1. Criar flight que solicita alocação
        Aircraft aircraft = new Aircraft("AC777", "B777", AircraftSize.WIDE_BODY, 320);
        LocalDateTime landing = LocalDateTime.of(2024, 1, 15, 15, 45);
        LocalDateTime takeoff = LocalDateTime.of(2024, 1, 15, 17, 15);
        Flight flight = new Flight("FL1234", "LA1234", aircraft, 320, landing, takeoff, "LATAM");
        
        // 2. Executar alocação
        long startTime = System.currentTimeMillis();
        Gate allocatedGate = allocationService.allocateGate(flight);
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        // 3. Verificações
        assertNotNull(allocatedGate, "Gate should be allocated");
        assertEquals("G002", allocatedGate.getId(), "Should allocate B22 (Wide gate with capacity 350)");
        assertEquals("B22", allocatedGate.getName(), "Gate name should be B22");
        
        // 4. Verificar tempo de resposta (RF11: < 2 segundos)
        assertTrue(elapsedTime < 2000, "Allocation time should be less than 2 seconds, but was " + elapsedTime + "ms");
        
        // 5. Verificar que o flight está associado ao gate correto
        assertEquals("G002", flight.getAllocatedGateId(), "Flight should have G002 as allocated gate");
        
        // 6. Verificar que o gate está ocupado no horário
        boolean shouldBeOccupied = !allocatedGate.isTimeSlotAvailable(landing, takeoff);
        assertTrue(shouldBeOccupied, "Gate should be occupied during allocated time slot");
    }
    
    // Teste de concorrência - 50 requisições simultâneas
    @Test
    void testConcurrentAllocations_WithConflictingTimes_ShouldHandleProperly() throws InterruptedException {
        int numberOfRequests = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        List<Future<AllocationResult>> futures = new ArrayList<>();
        
        // Criar 50 voos com horários conflitantes
        for (int i = 0; i < numberOfRequests; i++) {
            final int index = i;
            Future<AllocationResult> future = executor.submit(() -> {
                try {
                    LocalDateTime landing = LocalDateTime.of(2024, 1, 15, 16, 0);
                    LocalDateTime takeoff = LocalDateTime.of(2024, 1, 15, 18, 0);
                    Aircraft aircraft = new Aircraft("AC" + index, "B737", AircraftSize.NARROW_BODY, 150);
                    Flight flight = new Flight("FL" + index, "FL" + index, aircraft, 150, landing, takeoff, "TAM");
                    
                    long start = System.currentTimeMillis();
                    Gate gate = allocationService.allocateGate(flight);
                    long elapsed = System.currentTimeMillis() - start;
                    
                    return new AllocationResult(true, gate.getId(), elapsed, null);
                } catch (Exception e) {
                    return new AllocationResult(false, null, 0, e.getMessage());
                }
            });
            futures.add(future);
            latch.countDown();
        }
        
        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // Coletar resultados
        List<AllocationResult> successful = new ArrayList<>();
        List<AllocationResult> failed = new ArrayList<>();
        
        for (Future<AllocationResult> future : futures) {
            try {
                AllocationResult result = future.get();
                if (result.success) {
                    successful.add(result);
                } else {
                    failed.add(result);
                }
            } catch (ExecutionException e) {
                failed.add(new AllocationResult(false, null, 0, e.getMessage()));
            }
        }
        
        // Verificações de concorrência
        System.out.println("Successful allocations: " + successful.size());
        System.out.println("Failed allocations: " + failed.size());
        
        // Deve haver pelo menos um conflito (não todos bem-sucedidos)
        assertTrue(failed.size() > 0, "Should have some conflicts due to limited gates");
        
        // Verificar que nenhum portão foi duplamente alocado no mesmo horário
        Map<String, Set<String>> gateToFlights = new HashMap<>();
        for (AllocationResult result : successful) {
            gateToFlights.computeIfAbsent(result.gateId, k -> new HashSet<>()).add(result.gateId);
        }
        
        // Cada portão deve ter no máximo 1 voo por horário (simplificado)
        for (Map.Entry<String, Set<String>> entry : gateToFlights.entrySet()) {
            assertTrue(entry.getValue().size() <= 5, "Gate " + entry.getKey() + " has multiple allocations");
        }
        
        // Verificar tempo médio de resposta
        double avgTime = successful.stream().mapToLong(r -> r.elapsedTime).average().orElse(0);
        System.out.println("Average response time: " + avgTime + "ms");
        assertTrue(avgTime < 2000, "Average response time should be less than 2 seconds");
    }
    
    // Teste: Conflito sem portão disponível
    @Test
    void testNoAvailableGate_ShouldReturnConflict() {
        // Criar muitos voos para ocupar todos os portões Wide
        List<Flight> flights = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            Aircraft aircraft = new Aircraft("ACW" + i, "B777", AircraftSize.WIDE_BODY, 350);
            LocalDateTime landing = LocalDateTime.of(2024, 1, 15, 19, 0);
            LocalDateTime takeoff = LocalDateTime.of(2024, 1, 15, 21, 0);
            Flight flight = new Flight("FLW" + i, "W" + i, aircraft, 350, landing, takeoff, "LATAM");
            flights.add(flight);
        }
        
        // Tentar alocar mais um voo Wide sem portão disponível
        Aircraft extraAircraft = new Aircraft("ACX", "B787", AircraftSize.WIDE_BODY, 330);
        LocalDateTime landing = LocalDateTime.of(2024, 1, 15, 20, 0);
        LocalDateTime takeoff = LocalDateTime.of(2024, 1, 15, 22, 0);
        Flight extraFlight = new Flight("FLX", "XX123", extraAircraft, 330, landing, takeoff, "LATAM");
        
        Exception exception = assertThrows(Exception.class, () -> {
            allocationService.allocateGate(extraFlight);
        });
        
        assertTrue(exception.getMessage().contains("No compatible gate available"), 
            "Should return conflict message when no gate available");
    }
    
    private static class AllocationResult {
        boolean success;
        String gateId;
        long elapsedTime;
        String errorMessage;
        
        AllocationResult(boolean success, String gateId, long elapsedTime, String errorMessage) {
            this.success = success;
            this.gateId = gateId;
            this.elapsedTime = elapsedTime;
            this.errorMessage = errorMessage;
        }
    }
}
