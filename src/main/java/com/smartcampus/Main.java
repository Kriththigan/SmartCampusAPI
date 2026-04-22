package com.smartcampus;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    public SmartCampusApplication() {
        seedData();
    }

    private void seedData() {
        DataStore store = DataStore.getInstance();

        if (!store.getRooms().isEmpty()) return;

        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room room2 = new Room("CS-101", "Computer Science Lab", 30);
        Room room3 = new Room("ENG-205", "Engineering Workshop", 40);
        store.getRooms().put(room1.getId(), room1);
        store.getRooms().put(room2.getId(), room2);
        store.getRooms().put(room3.getId(), room3);

        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "CS-101");
        Sensor sensor3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "ENG-205");
        store.getSensors().put(sensor1.getId(), sensor1);
        store.getSensors().put(sensor2.getId(), sensor2);
        store.getSensors().put(sensor3.getId(), sensor3);

        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());
        room3.getSensorIds().add(sensor3.getId());
    }
}
