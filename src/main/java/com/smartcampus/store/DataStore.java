package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    
    private static final DataStore INSTANCE = new DataStore();

    
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(r1.getId(), r1);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        sensors.put(s1.getId(), s1);
        r1.getSensorIds().add(s1.getId());

        readings.put(s1.getId(), new ArrayList<>());
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    
    public Map<String, Room> getRooms() { return rooms; }

    public Room getRoom(String id) { return rooms.get(id); }

    public void addRoom(Room room) { rooms.put(room.getId(), room); }

    public boolean deleteRoom(String id) {
        return rooms.remove(id) != null;
    }

    public Map<String, Sensor> getSensors() { return sensors; }

    public Sensor getSensor(String id) { return sensors.get(id); }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        readings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }

    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
