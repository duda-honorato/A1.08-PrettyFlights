package com.prettyflights.repository;

import com.prettyflights.model.Gate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GateRepository {
    private Map<String, Gate> gates;
    
    public GateRepository() {
        this.gates = new ConcurrentHashMap<>();
    }
    
    public void save(Gate gate) {
        gates.put(gate.getId(), gate);
    }
    
    public void update(Gate gate) {
        gates.put(gate.getId(), gate);
    }
    
    public Optional<Gate> findById(String id) {
        return Optional.ofNullable(gates.get(id));
    }
    
    public List<Gate> findAll() {
        return new ArrayList<>(gates.values());
    }
    
    public void clear() {
        gates.clear();
    }
}
