# Smart Campus API

## Student Information

| Field | Details |
|---|---|
| Name | Satkunam Kriththigan |
| Module | 5COSC022C.2 Client-Server Architectures |
| University | University of Westminster |
| GitHub Repo | https://github.com/Kriththigan/SmartCampusAPI.git |
| Submission Date | 24 April 2026 |

---

A RESTful API built with **JAX-RS (Jersey)** and **Grizzly HTTP Server** for managing rooms and sensors across a university smart campus.

---

## Technology Stack

- Java 11
- JAX-RS (Jersey 2.35)
- Grizzly HTTP Server
- Jackson (JSON)
- Maven
- In-memory storage (ConcurrentHashMap)

---

## API Base URL

```
http://localhost:8080/api/v1
```

---

## Resource Hierarchy

```
/api/v1
├── /rooms
│   ├── GET       - list all rooms
│   ├── POST      - create a room
│   ├── GET       /{roomId} - get room by ID
│   └── DELETE    /{roomId} - delete room
├── /sensors
│   ├── GET       - list all sensors (optional ?type= filter)
│   ├── POST      - create a sensor
│   ├── GET       /{sensorId} - get sensor by ID
│   └── /{sensorId}/readings
│       ├── GET   - get all readings
│       └── POST  - add a new reading
└── GET / - API discovery endpoint
```

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- NetBeans IDE (recommended) or any Java IDE

### Step 1 — Clone the repository
```bash
git clone https://github.com/Kriththigan/SmartCampusAPI.git
cd SmartCampusAPI
```

### Step 2 — Build the project
```bash
mvn clean package
```

### Step 3 — Run the server
```bash
java -jar target/SmartCampusAPI-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or run directly from NetBeans:
- Open `Main.java`
- Press **F6** to run

### Step 4 — Verify server is running
```bash
curl http://localhost:8080/api/v1
```

---

## Seed Data

The server automatically loads sample data on startup:

| ID | Name | Type |
|---|---|---|
| LIB-301 | Library Quiet Study | Room |
| CS-101 | Computer Science Lab | Room |
| ENG-205 | Engineering Workshop | Room |
| TEMP-001 | Temperature Sensor | Sensor (ACTIVE) |
| CO2-001 | CO2 Sensor | Sensor (ACTIVE) |
| OCC-001 | Occupancy Sensor | Sensor (MAINTENANCE) |

---

## Complete API Endpoints Table

| Method | Endpoint | Description | Success Code | Error Codes |
|---|---|---|---|---|
| GET | /api/v1 | API discovery and HATEOAS links | 200 | - |
| GET | /api/v1/rooms | Get all rooms | 200 | - |
| POST | /api/v1/rooms | Create a new room | 201 | - |
| GET | /api/v1/rooms/{roomId} | Get room by ID | 200 | - |
| DELETE | /api/v1/rooms/{roomId} | Delete a room | - | 409 |
| GET | /api/v1/sensors | Get all sensors | 200 | - |
| GET | /api/v1/sensors?type={type} | Filter sensors by type | 200 | - |
| POST | /api/v1/sensors | Create a new sensor | 201 | - |
| POST | /api/v1/sensors | Create Sensor with INVALID roomId  | - | 422 |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings | 200 | - |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading | 201 | - |
| POST | /api/v1/sensors/{sensorId}/readings | Reading on MAINTENANCE Sensor | - | 403 |


---

## API Endpoints

### Discovery
**GET /api/v1**

Returns API metadata and HATEOAS links.

```bash
curl http://localhost:8080/api/v1
```

Response 200:
```json
{
    "contact": "admin@smartcampus.ac.uk",
    "resources": {
        "rooms": "/api/v1/rooms",
        "sensors": "/api/v1/sensors"
    },
    "api": "Smart Campus API",
    "version": "v1",
    "status": "running"
}
```

---

### Rooms

**GET /api/v1/rooms** — Get all rooms
```bash
curl http://localhost:8080/api/v1/rooms
```

**POST /api/v1/rooms** — Create a room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"NEW-101","name":"New Lecture Room","capacity":30}'
```

**GET /api/v1/rooms/{roomId}** — Get room by ID
```bash
curl http://localhost:8080/api/v1/rooms/LIB-301
```

---

### Sensors

**GET /api/v1/sensors** — Get all sensors
```bash
curl http://localhost:8080/api/v1/sensors
```

**GET /api/v1/sensors?type=CO2** — Filter by type
```bash
curl "http://localhost:8080/api/v1/sensors?type=CO2"
```

**POST /api/v1/sensors** — Create Valid Sensor
```bash
curl http://localhost:8080/api/v1/sensors
-H "Content-Type: application/json" \
-d '{
  "id": "TEMP-002",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 0,
  "roomId": "CS-101"
}'
```

---

### Sensor Readings

**GET /api/v1/sensors/{sensorId}/readings** — Get reading history
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

**POST /api/v1/sensors/{sensorId}/readings** — Add a reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":25.5}'
```

---

## Error Responses

### 409 Conflict — Delete room with active sensors
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```
Response:
```json
{
    "error": "Room Conflict",
    "message": "Room LIB-301 cannot be deleted as it still has active sensors assigned to it.",
    "status": "409"
}
```

### 422 Unprocessable Entity — Sensor with invalid roomId
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"FAKE-ROOM"}'
```
Response:
```json
{
    "error": "Unprocessable Entity",
    "message": "Room with ID 'FAKE-ROOM' does not exist. Please register the room first before adding a sensor.",
    "status": "422"
}
```

### 403 Forbidden — Reading on MAINTENANCE sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":10}'
```
Response:
```json
{
    "error": "Sensor Unavailable",
    "message": "Sensor 'OCC-001' is currently under MAINTENANCE and cannot accept new readings.",
    "status": "403"
}
```

---

## Report — Question Answers

### Part 1.1 — JAX-RS Resource Lifecycle
By default, JAX-RS creates a **new resource class instance for every incoming HTTP request** (per-request scope). This means each request gets a completely fresh object. Because of this, instance variables inside resource classes cannot be used to store shared state. To manage shared in-memory data safely across multiple concurrent requests, a **Singleton DataStore** class using `ConcurrentHashMap` is used. `ConcurrentHashMap` is thread-safe, preventing race conditions when multiple requests read or write data simultaneously.

### Part 1.2 — HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means embedding navigation links directly inside API responses. This allows clients to discover available actions dynamically without relying on external documentation. For example, the discovery endpoint returns links to `/api/v1/rooms` and `/api/v1/sensors`, so clients know exactly where to go next. This reduces coupling between client and server — if URLs change, clients following links automatically adapt without code changes.

### Part 2.1 — Returning IDs vs Full Room Objects
Returning only IDs is bandwidth-efficient but forces clients to make additional requests to fetch details for each room, causing the "N+1 problem" where one list request triggers N more requests. Returning full objects in a single response reduces round trips and is better for performance. However, full objects increase payload size. For this API, full objects are returned to minimise client-side processing and network round trips.

### Part 2.2 — DELETE Idempotency
In this implementation, DELETE is **partially idempotent**. The first call removes the room and returns 204 No Content. Subsequent calls return 404 Not Found because the room no longer exists. The server state is identical after each call (room is gone), which satisfies the idempotency requirement in terms of side effects. However, the different HTTP status codes (204 vs 404) mean the response is not identical, which some strict definitions consider non-idempotent.

### Part 3.1 — @Consumes Mismatch
If a client sends data with `Content-Type: text/plain` or `application/xml` to a method annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS automatically returns **HTTP 415 Unsupported Media Type** without invoking the resource method at all. The framework handles this mismatch at the request routing level, protecting the application code from receiving unexpected data formats.

### Part 3.2 — @QueryParam vs Path Segment
Query parameters (`?type=CO2`) are semantically correct for **filtering and searching** because the base resource `/sensors` remains the same — you are narrowing a collection, not accessing a different resource. A path segment like `/sensors/type/CO2` implies a distinct sub-resource exists at that path, which is semantically misleading. Query parameters are also optional by default, composable (multiple filters can be combined), and do not require additional route definitions. This makes them the superior choice for filtering collections.

### Part 4.1 — Sub-Resource Locator Benefits
The Sub-Resource Locator pattern delegates handling of nested paths to dedicated classes. `SensorReadingResource` only handles reading-related logic, while `SensorResource` handles sensor-related logic. This separation follows the Single Responsibility Principle — each class has one clear purpose. In large APIs, putting all nested paths in one massive controller class becomes unmaintainable and hard to test. Sub-resource locators allow each concern to be developed, tested, and evolved independently, improving code quality and maintainability.

### Part 5.1 — HTTP 422 vs 404
HTTP 404 means the **requested URL** was not found on the server. HTTP 422 means the URL was valid and the JSON payload was well-formed, but the **semantic content is invalid** — in this case, the `roomId` field references a room that does not exist. Using 422 gives clients precise feedback: the problem is inside the request body data, not the endpoint itself. This distinction helps client developers debug issues faster and write more accurate error handling logic.

### Part 5.2 — Stack Trace Security Risks
Exposing Java stack traces to external users reveals: internal package and class names (helps attackers map the codebase structure), library names and versions (enables targeting of known CVEs), internal logic flow and method call chains (aids in crafting targeted exploits), file system paths if present, and database query details if applicable. Attackers use this information to identify vulnerable dependencies and craft precise attacks. The GlobalExceptionMapper prevents this by always returning a generic error message instead of the raw stack trace.

### Part 5.3 — Filters vs Inline Logging
Using JAX-RS filters for logging follows the **separation of concerns** principle. Adding `Logger.info()` calls manually inside every resource method is repetitive, inconsistent, and error-prone — it is easy to forget to add logging to new methods. Filters are applied automatically to every request and response from a single class, ensuring consistent logging across the entire API. This also makes it easy to change the logging format or level globally without touching individual resource classes.

---

## Video Demonstration

https://drive.google.com/file/d/1U3RVllgBubFVywK_DEz1p4KLlCHDeIuL/view?usp=drive_link

---

## Project Structure

```
SmartCampusAPI/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── Main.java
    ├── config/
    │   └── JacksonConfig.java
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    ├── store/
    │   └── DataStore.java
    ├── resource/
    │   ├── DiscoveryResource.java
    │   ├── RoomResource.java
    │   ├── SensorResource.java
    │   ├── SensorReadingResource.java
    │   └── LoggingFilter.java
    └── exception/
        ├── RoomNotEmptyException.java
        ├── RoomNotEmptyExceptionMapper.java
        ├── LinkedResourceNotFoundException.java
        ├── LinkedResourceNotFoundExceptionMapper.java
        ├── SensorUnavailableException.java
        ├── SensorUnavailableExceptionMapper.java
        └── GlobalExceptionMapper.java
```
