package com.prettyflights.unit;

import com.prettyflights.model.*;
import com.prettyflights.model.enums.AircraftSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class GateUnitTest {
    
    private Gate narrowGate;
    private Gate wideGate;
    private Aircraft narrowAircraft;
    private Aircraft wideAircraft;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @BeforeEach
    void setUp() {
        narrowGate = new Gate("G001", "A10", AircraftSize.NARROW_BODY, 200);
        wideGate = new Gate("G002", "B22", AircraftSize.WIDE_BODY, 350);
        
        narrowAircraft = new Aircraft("AC001", "A320", AircraftSize.NARROW_BODY, 180);
        wideAircraft = new Aircraft("AC002", "B777", AircraftSize.WIDE_BODY, 320);
        
        startTime = LocalDateTime.of(2024, 1, 15, 15, 0);
        endTime = LocalDateTime.of(2024, 1, 15, 17, 0);
    }
    
    // Teste 1: Compatibilidade de tamanho
    @Test
    void testGateCompatibility_WideBodyOnNarrowGate_ShouldReturnFalse() {
        // Critério: Wide Body NÃO deve ser compatível com Narrow Gate
        boolean compatible = narrowGate.isCompatibleWith(wideAircraft);
        assertFalse(compatible, "Wide body aircraft should NOT be compatible with narrow gate");
    }
    
    @Test
    void testGateCompatibility_NarrowBodyOnWideGate_ShouldReturnTrue() {
        // Wide Gate deve aceitar Narrow Body
        boolean compatible = wideGate.isCompatibleWith(narrowAircraft);
        assertTrue(compatible, "Narrow body aircraft should be compatible with wide gate");
    }
    
    // Teste 2: Sobreposição de horário
    @Test
    void testTimeSlotAvailability_Overlapping_ShouldReturnFalse() {
        // Ocupar o horário 15:00-17:00
        narrowGate.reserveTimeSlot(startTime, endTime, "FL001");
        
        // Tentar alocar outro voo no mesmo horário
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 15, 15, 30);
        LocalDateTime newEnd = LocalDateTime.of(2024, 1, 15, 16, 30);
        
        boolean available = narrowGate.isTimeSlotAvailable(newStart, newEnd);
        assertFalse(available, "Time slot overlapping should not be available");
    }
    
    @Test
    void testTimeSlotAvailability_NonOverlapping_ShouldReturnTrue() {
        narrowGate.reserveTimeSlot(startTime, endTime, "FL001");
        
        // Tentar alocar em horário diferente
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 15, 17, 30);
        LocalDateTime newEnd = LocalDateTime.of(2024, 1, 15, 19, 30);
        
        boolean available = narrowGate.isTimeSlotAvailable(newStart, newEnd);
        assertTrue(available, "Non-overlapping time slot should be available");
    }
    
    // Teste 3: Fluxo de passageiros
    @Test
    void testPassengerFlow_ExceedsCapacity_ShouldReturnFalse() {
        // Portão Narrow com capacidade 200, passageiros 350
        boolean isValid = narrowGate.validatePassengerFlow(350);
        assertFalse(isValid, "Passenger flow exceeding capacity should be rejected");
    }
    
    @Test
    void testPassengerFlow_WithinCapacity_ShouldReturnTrue() {
        boolean isValid = narrowGate.validatePassengerFlow(150);
        assertTrue(isValid, "Passenger flow within capacity should be accepted");
    }
    
    @Test
    void testPassengerFlow_ExactCapacity_ShouldReturnTrue() {
        boolean isValid = narrowGate.validatePassengerFlow(200);
        assertTrue(isValid, "Passenger flow at exact capacity should be accepted");
    }
}
